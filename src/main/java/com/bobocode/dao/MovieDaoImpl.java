package com.bobocode.dao;

import com.bobocode.exception.DaoOperationException;
import com.bobocode.model.Movie;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieDaoImpl implements MovieDao {
    private static final String INSERT_SQL = "INSERT INTO movie (name, duration, release_date) VALUES (?, ?, ?);";
    private static final String SELECT_BY_NAME_SQL = "SELECT * FROM movie WHERE name=?;";
    private DataSource dataSource;

    public MovieDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Movie movie) {
        Objects.requireNonNull(movie);
        try (Connection connection = dataSource.getConnection()) {
            saveMovie(connection, movie);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't save movie " + movie, e);
        }
    }

    private void saveMovie(Connection connection, Movie movie) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, movie);
        executeUpdate(insertStatement);
        Long id = fetchGeneratedId(insertStatement);
        movie.setId(id);
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
            throw new DaoOperationException("Can not obtain product ID");
        }
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Movie movie) {
        try {
            PreparedStatement insertPreparedStatement = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillInsertStatementWithParameters(insertPreparedStatement, movie);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't prepare statement for movie" + movie, e);
        }
    }

    private PreparedStatement fillInsertStatementWithParameters(PreparedStatement preparedStatement, Movie movie) throws SQLException {
        preparedStatement.setString(1, movie.getName());
        preparedStatement.setLong(2, movie.getDuration());
        preparedStatement.setDate(3, Date.valueOf(movie.getReleaseDate()));
        return preparedStatement;
    }

    @Override
    public List<Movie>  findByName(String name) {
        Objects.requireNonNull(name);
        try (Connection connection = dataSource.getConnection()) {
            return findMovieByName(name, connection);
        } catch (SQLException e) {
            throw new DaoOperationException("Error finding movies by name" + name, e);
        }
    }

    private List<Movie> findMovieByName(String name, Connection connection) throws SQLException {
        PreparedStatement selectByIdPreparedStatement = prepareSelectByIdStatement(name, connection);
        ResultSet resultSet = selectByIdPreparedStatement.executeQuery();
        List<Movie> movieList = collectToList(resultSet);
        if(movieList.size()==0) {
            throw new DaoOperationException(String.format("Movie with name = %s does not exist", name));
        }
        return movieList;
    }

    private List<Movie> collectToList(ResultSet resultSet) throws SQLException {
        List<Movie> movieList = new ArrayList<>();
        while (resultSet.next()){
            Movie movie = parseRow(resultSet);
            movieList.add(movie);
        }
        return movieList;
    }

    private Movie parseRow(ResultSet resultSet) {
        try {
            return createFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't parse row to create movie instance", e);
        }
    }

    private Movie createFromResultSet(ResultSet resultSet) throws SQLException {
        Movie movie = new Movie();
        movie.setId(resultSet.getLong("id"));
        movie.setName(resultSet.getString("name"));
        movie.setDuration(resultSet.getLong("duration"));
        movie.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        return movie;
    }

    private PreparedStatement prepareSelectByIdStatement(String name, Connection connection) {
        try {
            PreparedStatement selectByIdPreparedStatement = connection.prepareStatement(SELECT_BY_NAME_SQL);
            selectByIdPreparedStatement.setString(1, name);
            return selectByIdPreparedStatement;
        } catch (SQLException e) {
            throw new DaoOperationException("Couldn't prepare select by name statement with name=" + name, e);
        }
    }


    @Override
    public List<Movie> findAll() {
        return null;
    }



    @Override
    public List findByActorFirstAndLastName(String actorFirstName, String ActorLastName) {
        return null;
    }
}
