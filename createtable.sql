DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies (
	id VARCHAR(10),
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

Create table stars (
	id VARCHAR(10),
    name VARCHAR(100) NOT NULL,
    birthYear INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE stars_in_movies (
	starID VARCHAR(10) NOT NULL, -- referencing stars.id
    movieID VARCHAR(10) NOT NULL, -- referencing movies.id
    FOREIGN KEY (starID) REFERENCES stars(id),
    FOREIGN KEY (movieID) REFERENCES movies(id)
);

CREATE TABLE genres (
	id INTEGER AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE genres_in_movies (
	genreID INTEGER NOT NULL, -- referencing genres.id
    movieID VARCHAR(10) NOT NULL, -- referencing movies.id
    FOREIGN KEY (genreID) REFERENCES genres(id)
);

CREATE TABLE creditcards (
	id VARCHAR(20),
	firstName VARCHAR(50) NOT NULL,
	lastName VARCHAR(50) NOT NULL,
	expiration DATE NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE customers (
	id INTEGER AUTO_INCREMENT,
	firstName VARCHAR(50) NOT NULL,
	lastName VARCHAR(50) NOT NULL,
	ccId VARCHAR(20) NOT NULL, -- referencing creditcards.id
	address VARCHAR(200) NOT NULL,
	email VARCHAR(50) NOT NULL,
	password VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (ccID) REFERENCES creditcards(id)
);


CREATE TABLE sales (
	id INTEGER AUTO_INCREMENT,
	customerId INTEGER NOT NULL, -- referencing customers.id
	movieId VARCHAR(10) NOT NULL, -- referencing movies.id
	saleDate DATE NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (customerID) REFERENCES customers(id),
    FOREIGN KEY (movieID) REFERENCES movies(id)
);

CREATE TABLE ratings (
	movieId VARCHAR(10), -- referencing movies.id
	rating FLOAT NOT NULL,
	numVotes INTEGER NOT NULL,
    FOREIGN KEY (movieID) REFERENCES movies(id)
);