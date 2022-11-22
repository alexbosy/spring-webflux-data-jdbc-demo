CREATE TABLE customers
(
    id                   SERIAL PRIMARY KEY,
    date_of_birth        DATE        NOT NULL,
    country_of_residence VARCHAR(2)  NOT NULL,
    identity_number      VARCHAR(30),
    passport_number      VARCHAR(30),
    registration_ip      VARCHAR(15) NOT NULL,
    registration_country VARCHAR(2)  NOT NULL,
    user_id              INT         NOT NULL,
    UNIQUE (user_id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);




