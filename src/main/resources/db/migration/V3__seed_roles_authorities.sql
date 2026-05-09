-- Roles (Spring hasRole("X") checks authority ROLE_X — use ROLE_ prefix in DB)
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Fine-grained authorities (hasAuthority("..."))
INSERT INTO authorities (name) VALUES ('DOCUMENT_READ');
INSERT INTO authorities (name) VALUES ('DOCUMENT_WRITE');
INSERT INTO authorities (name) VALUES ('USER_MANAGE');

-- ROLE_USER (id 1): read + write documents
-- ROLE_ADMIN (id 2): all including user management
INSERT INTO role_authorities (role_id, authority_id) VALUES
    (1, 1),
    (1, 2),
    (2, 1),
    (2, 2),
    (2, 3);

-- Assign ROLE_USER to any existing account that has no roles yet
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, 1
FROM users u
WHERE NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id);
