CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE authorities (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    CONSTRAINT uq_authorities_name UNIQUE (name)
);

CREATE TABLE role_authorities (
    role_id       BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    authority_id  BIGINT NOT NULL REFERENCES authorities (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, authority_id)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);
