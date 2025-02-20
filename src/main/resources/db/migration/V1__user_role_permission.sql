CREATE TABLE permissions (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL
);

CREATE TABLE permission_role (
    role_id       BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE ,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(128) NOT NULL,
    last_name   VARCHAR(128) NOT NULL,
    role_id     INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE
);

INSERT INTO roles (name) VALUES ('CANDIDATE'), ('INTERVIEWER');
