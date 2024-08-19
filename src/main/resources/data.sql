INSERT INTO roles (role_name) VALUES ('USER');
INSERT INTO roles (role_name) VALUES ('ADMIN');

INSERT INTO users (name, email, phone_number, user_password)
VALUES ('John Doe', 'john.doe@example.com', '1234567890', '$2a$10$eurrNpnzjpaW5q2SASAtRuIHyYGTPEtUMw3aQJNa7QocAfAlhx2Wu');
INSERT INTO users (name, email, phone_number, user_password)
VALUES ('Jane Smith', 'jane.smith@example.com', '0987654321', '$2a$10$eurrNpnzjpaW5q2SASAtRuIHyYGTPEtUMw3aQJNa7QocAfAlhx2Wu');
-- Insert devices
INSERT INTO devices (name, description, type, status_type, user_id)
VALUES ('Device1', 'Description for Device1', 'Type1', 'ONLINE', 1); -- Assigned to John Doe (user_id 1)

INSERT INTO devices (name, description, type, status_type, user_id)
VALUES ('Device2', 'Description for Device2', 'Type2', 'OFFLINE', 1); -- Assigned to John Doe (user_id 1)

INSERT INTO devices (name, description, type, status_type, user_id)
VALUES ('Device3', 'Description for Device3', 'Type3', 'ONLINE', 2); -- Assigned to Jane Smith (user_id 2)


INSERT INTO users_roles (user_id, role_id) VALUES (1, 1); -- John Doe has USER role
INSERT INTO users_roles (user_id, role_id) VALUES (1, 2); -- John Doe has ADMIN role
INSERT INTO users_roles (user_id, role_id) VALUES (2, 1);
