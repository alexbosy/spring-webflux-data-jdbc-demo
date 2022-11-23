--changeset alexbo:init
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    login    VARCHAR(20)  NOT NULL UNIQUE,
    name     VARCHAR(20)  NOT NULL,
    surname  VARCHAR(30)  NOT NULL,
    email    VARCHAR(25)  NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL
);