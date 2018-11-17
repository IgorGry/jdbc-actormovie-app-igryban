package com.bobocode;

import com.bobocode.dao.ActorDao;
import com.bobocode.dao.ActorDaoImpl;
import com.bobocode.exception.DaoOperationException;
import com.bobocode.model.Actor;
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

    @BeforeClass
    public static void init() throws SQLException {
        DataSource defaultPostgresDataSource = JdbcUtil.createDefaultPostgresDataSource();
        ActorMovieDbInitializer dbInitializer = new ActorMovieDbInitializer(defaultPostgresDataSource);
        dbInitializer.init();
        actorDao = new ActorDaoImpl(defaultPostgresDataSource);
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
}
