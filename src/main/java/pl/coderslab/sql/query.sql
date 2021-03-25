CREATE DATABASE movies_library
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE movies_library;

CREATE TABLE movies (
  movie_id int AUTO_INCREMENT,
  movie_name VARCHAR(80) NOT NULL,
  movie_date YEAR,
  movie_length int,
PRIMARY KEY (movie_id)
);