--liquibase formatted sql
--changeset student:1
CREATE TABLE IF NOT EXISTS company (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    employees_count INT NOT NULL
    );