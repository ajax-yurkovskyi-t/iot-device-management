CREATE TABLE roles (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      role_name VARCHAR(255) NOT NULL UNIQUE
);



CREATE TABLE users (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      phone_number VARCHAR(255) NOT NULL UNIQUE,
                      user_password VARCHAR(255) NOT NULL
);

CREATE TABLE devices (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description VARCHAR(255) NOT NULL,
                        type VARCHAR(255) NOT NULL,
                        status_type VARCHAR(255) NOT NULL,
                        user_id BIGINT,
                        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
