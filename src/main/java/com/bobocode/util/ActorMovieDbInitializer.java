package com.bobocode.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ActorMovieDbInitializer {
    private final static String TABLE_INITIALIZATION_SQL_FILE = "db/migration/db_initialization.sql";
    private DataSource dataSource;

    public ActorMovieDbInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init() throws SQLException {
        String createTablesSql = FileReader.readWholeFileFromResources(TABLE_INITIALIZATION_SQL_FILE);

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(createTablesSql);
        }
    }
}
