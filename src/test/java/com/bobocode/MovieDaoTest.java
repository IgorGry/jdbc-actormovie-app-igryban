package com.bobocode;

import com.bobocode.dao.MovieDao;
import com.bobocode.dao.MovieDaoImpl;
import com.bobocode.exception.DaoOperationException;
import com.bobocode.model.Movie;
import com.bobocode.util.ActorMovieDbInitializer;
import com.bobocode.util.JdbcUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class MovieDaoTest {
    private static MovieDao movieDao;

    @BeforeClass
    public static void init() throws SQLException {
        DataSource defaultPostgresDataSource = JdbcUtil.createDefaultPostgresDataSource();
        ActorMovieDbInitializer dbInitializer = new ActorMovieDbInitializer(defaultPostgresDataSource);
        dbInitializer.init();
        movieDao = new MovieDaoImpl(defaultPostgresDataSource);
    }


    @Test
    public void testSave() {
        Movie movieTomHanks = Movie.builder().name("Forrest Gump").duration(200000L).releaseDate(LocalDate.of(1994, 9, 8)).build();
        movieDao.save(movieTomHanks);
        assertNotNull(movieTomHanks.getId());

    }

    @Test
    public void testSaveInvalidMovie() {
        Movie invalidTestActor = Movie.builder().name(null).duration(200000L).releaseDate(LocalDate.of(1994, 9, 8)).build();
        try {
            movieDao.save(invalidTestActor);
            fail("Exception was't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals("Couldn't save movie " + invalidTestActor, e.getMessage());
        }
    }

    @Test
    public void testFindByName() {
        Movie testMovie = Movie.builder().name("Sherlock Holmes").duration(250000L).releaseDate(LocalDate.of(2010, 1, 7)).build();
        movieDao.save(testMovie);

        List<Movie> movies = movieDao.findByName(testMovie.getName());
        Movie savedMovie = movies.stream().filter(movie->movie.equals(testMovie)).findAny().get();

        assertEquals(testMovie, savedMovie);
        assertEquals(testMovie.getName(), savedMovie.getName());
        assertEquals(testMovie.getDuration(), savedMovie.getDuration());
        assertEquals(testMovie.getReleaseDate(), savedMovie.getReleaseDate());
    }

    @Test
    public void testFindByNotExistingName() {
        String invalidName = "404movie";
        try {
            movieDao.findByName(invalidName);
            fail("Exception was't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals(String.format("Movie with name = %s does not exist", invalidName), e.getMessage());
        }
    }

}
