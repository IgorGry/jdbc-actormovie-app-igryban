package com.bobocode;

import com.bobocode.util.ActorMovieDbInitializer;
import com.bobocode.util.JdbcUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class ActorMovieDbInitializerTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void init() throws SQLException{
        dataSource = JdbcUtil.createDefaultPostgresDataSource();
        ActorMovieDbInitializer dbInitializer = new ActorMovieDbInitializer(dataSource);
        dbInitializer.init();
    }

    @Test
    public void testTablesExistandHaveCorrectNames() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT tablename FROM pg_catalog.pg_tables where tablename in ('actor', 'movie')");
            List<String> tableNames = fetchTableNames(resultSet);

            assertThat(tableNames, containsInAnyOrder("actor", "movie"));
        }
    }
    private List<String> fetchTableNames(ResultSet resultSet) throws SQLException {
        List<String> tableNamesList = new ArrayList<>();
        while (resultSet.next()) {
            String tableName = resultSet.getString("tablename");
            tableNamesList.add(tableName);
        }
        return tableNamesList;
    }
}
