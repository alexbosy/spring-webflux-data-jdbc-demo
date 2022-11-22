--changeset alexbo:init
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    login    VARCHAR(20),
    name     VARCHAR(20),
    surname  VARCHAR(30),
    email    VARCHAR(25),
    password VARCHAR(100)
);
