package com.bobocode;

import com.bobocode.dao.ActorDao;
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class MovieDaoTest {
    private static MovieDao movieDao;
    private static ActorDao actorDao;

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
        Movie testMovieWithSameName = Movie.builder().name("Sherlock Holmes").duration(230000L).releaseDate(LocalDate.of(2009, 1, 7)).build();

        movieDao.save(testMovie);
        movieDao.save(testMovieWithSameName);

        List<Movie> movies = movieDao.findByName(testMovie.getName());
        Movie savedMovie = movies.stream().filter(movie -> movie.equals(testMovie)).findAny().get();

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

    @Test
    public void testFindAll() {
        List<Movie> newMovies = createTestMovieList();
        List<Movie> oldMovies = movieDao.findAll();
        newMovies.forEach(movieDao::save);

        List<Movie> movies = movieDao.findAll();

        assertTrue(movies.containsAll(newMovies));
        assertTrue(movies.containsAll(oldMovies));
        assertEquals(oldMovies.size() + newMovies.size(), movies.size());

    }

    private List<Movie> createTestMovieList() {
        return List.of(
                Movie.builder()
                        .name("Alexander")
                        .duration(253000L)
                        .releaseDate(LocalDate.of(2004, 1, 3)).build(),
                Movie.builder()
                        .name("The Illusionist")
                        .duration(269000L)
                        .releaseDate(LocalDate.of(2006, 3, 4)).build(),
                Movie.builder()
                        .name("Suicide Squad")
                        .duration(289000L)
                        .releaseDate(LocalDate.of(2016, 5, 12)).build(),
                Movie.builder()
                        .name("Dallas Buyers Club")
                        .duration(256900L)
                        .releaseDate(LocalDate.of(2013, 11, 27)).build()
        );
    }

    @Test
    public void testFindByActorFirstAndLastName() {
        List<Movie> movies = movieDao.findByActorFirstAndLastName("Jared", "Leto");
        assertThat(movies.size(), equalTo(3));
        Movie fightClab = movieDao.findByName("Fight Club").get(0);
        Movie dallasBuyersClub = movieDao.findByName("Dallas Buyers Club").get(0);
        Movie alexander = movieDao.findByName("Alexander").get(0);
        assertTrue(movies.contains(fightClab));
        assertTrue(movies.contains(dallasBuyersClub));
        assertTrue(movies.contains(alexander));
        //todo modify test method

    }


}
