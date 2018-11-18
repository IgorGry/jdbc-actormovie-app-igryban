package com.bobocode.dao;

import com.bobocode.exception.DaoOperationException;
import com.bobocode.model.Actor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

public class ActorDaoImpl implements ActorDao {
    private static final String INSERT_SQL = "INSERT INTO actor (first_name, last_name, birthday) VALUES (?, ?, ?);";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM actor WHERE id=?;";
    private static final String INSERT_ACTOR_MOVIE_LINK_SQL = "INSERT INTO actor_movie (actor_id, movie_id) VALUES (?, ?);";
    private DataSource dataSource;

    public ActorDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Actor actor) {
        Objects.requireNonNull(actor);
        try (Connection connection = dataSource.getConnection()) {
            saveActor(connection, actor);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't save actor " + actor, e);
        }
    }

    private void saveActor(Connection connection, Actor actor) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, actor);
        executeUpdate(insertStatement);
        Long id = fetchGeneratedId(insertStatement);
        actor.setId(id);
    }

    private void executeUpdate(PreparedStatement insertStatement) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoOperationException("Nothing has been changed");
        }
    }

    private Long fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new DaoOperationException("Can not obtain actor ID");
        }
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Actor actor) {
        try {
            PreparedStatement insertPreparedStatement = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillInsertStatementWithParameters(insertPreparedStatement, actor);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't prepare statement for actor" + actor, e);
        }
    }

    private PreparedStatement fillInsertStatementWithParameters(PreparedStatement preparedStatement, Actor actor) throws SQLException {
        preparedStatement.setString(1, actor.getFirstName());
        preparedStatement.setString(2, actor.getLastName());
        preparedStatement.setDate(3, Date.valueOf(actor.getBirthday()));
        return preparedStatement;
    }

    @Override
    public Actor findById(Long id) {
        Objects.requireNonNull(id);
        try (Connection connection = dataSource.getConnection()) {
            return findActorById(id, connection);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't find actor with id" + id, e);
        }
    }

    private Actor findActorById(Long id, Connection connection) throws SQLException {
        PreparedStatement selectByIdPreparedStatement = prepareSelectByIdStatement(id, connection);
        ResultSet resultSet = selectByIdPreparedStatement.executeQuery();
        if (resultSet.next()) {
            return parseRow(resultSet);
        } else {
            throw new DaoOperationException(String.format("Actor with id = %d does not exist", id));
        }
    }

    private Actor parseRow(ResultSet resultSet) {
        try {
            return createFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't parse row to create actor instance", e);
        }
    }

    private Actor createFromResultSet(ResultSet resultSet) throws SQLException {
        Actor actor = new Actor();
        actor.setId(resultSet.getLong("id"));
        actor.setFirstName(resultSet.getString("first_name"));
        actor.setLastName(resultSet.getString("last_name"));
        actor.setBirthday(resultSet.getDate("birthday").toLocalDate());
        return actor;
    }

    private PreparedStatement prepareSelectByIdStatement(Long id, Connection connection) {
        try {
            PreparedStatement selectByIdPreparedStatement = connection.prepareStatement(SELECT_BY_ID_SQL);
            selectByIdPreparedStatement.setLong(1, id);
            return selectByIdPreparedStatement;
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't prepare select by id prepared statement with id=" + id, e);
        }
    }

    public void verifyActorId(Long id, Connection connection) throws SQLException {
        if (id == null) {
            throw new DaoOperationException("Cannot find a actor without ID");
        }
        findActorById(id, connection);
    }

    @Override
    public void linkActorToMovieByID(Long actorId, Long movieId) {
        try (Connection connection = dataSource.getConnection()) {
            verifyActorId(actorId, connection);
            MovieDaoImpl movieDao = new MovieDaoImpl(dataSource);
            movieDao.verifyMovieId(movieId, connection);
            PreparedStatement insertActorMovieLinkPreparedStatement = prepareInsertActorMovieLinkStatement(connection, actorId, movieId);
            executeUpdate(insertActorMovieLinkPreparedStatement);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't link actor to movie by id");
        }
    }

    private PreparedStatement prepareInsertActorMovieLinkStatement(Connection connection, Long actorId, Long movieId) {
        try {
            PreparedStatement insertActorMovieLinkPreparedStatement = connection.prepareStatement(INSERT_ACTOR_MOVIE_LINK_SQL);
            insertActorMovieLinkPreparedStatement.setLong(1, actorId);
            insertActorMovieLinkPreparedStatement.setLong(2, movieId);
            return insertActorMovieLinkPreparedStatement;
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't prepare statement for insert actor movie link for actor id" + actorId + " movieID" + movieId, e);
        }
    }
}
