package com.bobocode;

import com.bobocode.dao.ActorDao;
import com.bobocode.dao.ActorDaoImpl;
import com.bobocode.dao.MovieDao;
import com.bobocode.dao.MovieDaoImpl;
import com.bobocode.exception.DaoOperationException;
import com.bobocode.model.Actor;
import com.bobocode.model.Movie;
import com.bobocode.util.ActorMovieDbInitializer;
import com.bobocode.util.JdbcUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class ActorDaoTest {
    private static ActorDao actorDao;
    private static MovieDao movieDao;

    @BeforeClass
    public static void init() throws SQLException {
        DataSource defaultPostgresDataSource = JdbcUtil.createDefaultPostgresDataSource();
        ActorMovieDbInitializer dbInitializer = new ActorMovieDbInitializer(defaultPostgresDataSource);
        dbInitializer.init();
        actorDao = new ActorDaoImpl(defaultPostgresDataSource);
        movieDao = new MovieDaoImpl(defaultPostgresDataSource);
    }

    @Test
    public void testSave() {
        Actor actorTomHanks = Actor.builder().firstName("Tom").lastName("Hanks").birthday(LocalDate.of(1956, 7, 9)).build();
        actorDao.save(actorTomHanks);
        assertNotNull(actorTomHanks.getId());

    }

    @Test
    public void testSaveInvalidActor() {
        Actor invalidTestActor = Actor.builder().firstName(null).lastName("Hanks").birthday(LocalDate.of(1956, 7, 9)).build();
        try {
            actorDao.save(invalidTestActor);
            fail("Exception was't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals("Couldn't save actor " + invalidTestActor, e.getMessage());
        }
    }

    @Test
    public void testFindById() {
        Actor testActor = Actor.builder().firstName("Brad").lastName("Pitt").birthday(LocalDate.of(1963, 12, 18)).build();
        actorDao.save(testActor);

        Actor actor = actorDao.findById(testActor.getId());

        assertEquals(testActor, actor);
        assertEquals(testActor.getFirstName(), actor.getFirstName());
        assertEquals(testActor.getLastName(), actor.getLastName());
        assertEquals(testActor.getBirthday(), actor.getBirthday());
    }

    @Test
    public void testFindByNotExistingId() {
        long invalidId = -1L;
        try {
            actorDao.findById(invalidId);
            fail("Exception was't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals(String.format("Actor with id = %d does not exist", invalidId), e.getMessage());
        }
    }

    @Test
    public void testLinkActorToMovieByNotExistingActorID() {
        Long notExistingActorID = 888L;
        Movie movieID = Movie.builder().name("Fight Club").duration(200000L).releaseDate(LocalDate.of(1996, 3, 22)).build();
        movieDao.save(movieID);
        Long existingMovieID = movieID.getId();
        try {
            actorDao.linkActorToMovieByID(notExistingActorID, existingMovieID);
            fail("Exception wasn't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals(String.format("Actor with id = %d does not exist", notExistingActorID), e.getMessage());
        }
    }

    @Test
    public void testLinkActorToMovieByNotExistingMovieID() {
        Actor actorID = Actor.builder().firstName("Edward").lastName("Norton").birthday(LocalDate.of(1969, 8, 18)).build();
        actorDao.save(actorID);
        Long notExistingmovieID = 888L;
        Long existingActorID = actorID.getId();

        try {
            actorDao.linkActorToMovieByID(existingActorID, notExistingmovieID);
            fail("Exception wasn't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals(String.format("Movie with id = %s does not exist", notExistingmovieID), e.getMessage());
        }
    }

    @Test
    public void testLinkActorToMovieByNullActorID() {
        Long nullActorID = null;
        Movie movieID = Movie.builder().name("Ocean's Thirteen").duration(180000L).releaseDate(LocalDate.of(2007, 2, 28)).build();
        movieDao.save(movieID);
        Long existingMovieID = movieID.getId();

        try {
            actorDao.linkActorToMovieByID(nullActorID, existingMovieID);
            fail("Exception wasn't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals("Cannot find a actor without ID", e.getMessage());
        }
    }

    @Test
    public void testLinkActorToMovieByNullMovieID() {
        Actor actorID = Actor.builder().firstName("Matt").lastName("Damon").birthday(LocalDate.of(1970, 10, 8)).build();
        Long nullMovieID = null;
        actorDao.save(actorID);
        Long existingActorID = actorID.getId();

        try {
            actorDao.linkActorToMovieByID(existingActorID, nullMovieID);
            fail("Exception wasn't thrown");
        } catch (Exception e) {
            assertEquals(DaoOperationException.class, e.getClass());
            assertEquals("Cannot find a movie without ID", e.getMessage());
        }
    }

    @Test
    public void testLinkActorToMovie() {
        Actor jerardLeto = Actor.builder().firstName("Jared").lastName("Leto").birthday(LocalDate.of(1981, 6, 22)).build();
        Actor angelinaJoly = Actor.builder().firstName("Angelina").lastName("Jolie").birthday(LocalDate.of(1973, 12, 27)).build();
        Actor nikolasCage = Actor.builder().firstName("Nicolas").lastName("Cage").birthday(LocalDate.of(1969, 9, 2)).build();
        actorDao.save(jerardLeto);
        actorDao.save(angelinaJoly);
        actorDao.save(nikolasCage);
        Movie fightClab = movieDao.findByName("Fight Club").get(0);
        Movie dallasBuyersClub = movieDao.findByName("Dallas Buyers Club").get(0);
        Movie alexander = movieDao.findByName("Alexander").get(0);
        if (fightClab != null) {
            actorDao.linkActorToMovieByID(jerardLeto.getId(), fightClab.getId());
        }
        if (dallasBuyersClub != null) {
            actorDao.linkActorToMovieByID(jerardLeto.getId(), dallasBuyersClub.getId());
        }
        if (alexander != null) {
            actorDao.linkActorToMovieByID(jerardLeto.getId(), alexander.getId());
            actorDao.linkActorToMovieByID(angelinaJoly.getId(), alexander.getId());
        }

        //todo add to this test logic using Movie.findByActorFirstAndLastName

    }
}

