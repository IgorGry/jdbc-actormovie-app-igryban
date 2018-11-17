CREATE TABLE IF NOT EXISTS actor (
  id         BIGINT,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  birthday DATE NOT NULL,
  CONSTRAINT PK_actor PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS movie (
  id            BIGINT,
  name          VARCHAR(255) NOT NULL,
  duration      FLOAT NOT NULL,
  release_date  DATE NOT NULL,
  CONSTRAINT PK_movie PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS actor_movie (
  actor_id      BIGINT NOT NULL,
  movie_id     BIGINT NOT NULL,
  CONSTRAINT PK_actor_movie PRIMARY KEY (actor_id, movie_id),
  CONSTRAINT FK_actor_movie_actor FOREIGN KEY (actor_id) REFERENCES actor,
  CONSTRAINT FK_actor_movie_movie FOREIGN KEY (movie_id) REFERENCES movie
);
