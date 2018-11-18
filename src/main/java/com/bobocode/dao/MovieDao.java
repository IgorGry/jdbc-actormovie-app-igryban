package com.bobocode.dao;

import com.bobocode.model.Movie;

import java.util.List;

public interface MovieDao {
    void save(Movie movie);

    List<Movie> findAll();

    List<Movie> findByName(String name);

    List<Movie> findByActorFirstAndLastName(String actorFirstName, String actorLastName);
}
