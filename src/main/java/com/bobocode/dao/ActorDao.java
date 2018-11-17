package com.bobocode.dao;

import com.bobocode.model.Actor;

public interface ActorDao {
    void save(Actor actor);

    Actor findById(Long id);

    void linkActorToMovieByID(Long actorId, Long movieId);
}
