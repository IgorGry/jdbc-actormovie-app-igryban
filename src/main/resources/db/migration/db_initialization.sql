CREATE TABLE IF NOT EXISTS actor (
  id SERIAL,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  birthday TIMESTAMP NOT NULL,
  CONSTRAINT PK_actor PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS movie (
  id SERIAL,
  name          VARCHAR(255) NOT NULL,
  duration      BIGINT NOT NULL,
  release_date  TIMESTAMP NOT NULL,
  CONSTRAINT PK_movie PRIMARY KEY (id),
  CONSTRAINT UQ_name_duration_release_date UNIQUE (name, duration, release_date)
);


CREATE TABLE IF NOT EXISTS actor_movie (
  actor_id      BIGINT NOT NULL,
  movie_id     BIGINT NOT NULL,
  CONSTRAINT PK_actor_movie PRIMARY KEY (actor_id, movie_id),
  CONSTRAINT FK_actor_movie_actor FOREIGN KEY (actor_id) REFERENCES actor,
  CONSTRAINT FK_actor_movie_movie FOREIGN KEY (movie_id) REFERENCES movie
);
