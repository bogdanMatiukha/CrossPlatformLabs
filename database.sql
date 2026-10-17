CREATE DATABASE IF NOT EXISTS university_property_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE university_property_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS property_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inventory_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL,
    status_item VARCHAR(50) NOT NULL
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS issue_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    property_id INT NOT NULL,
    request_status VARCHAR(50) NOT NULL DEFAULT 'СТВОРЕНО',
    urgency_f1 INT DEFAULT 0,
    criticality_f2 INT DEFAULT 0,
    deficit_f3 INT DEFAULT 0,
    integral_index DOUBLE DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES property_items(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transfer_records (
     id INT AUTO_INCREMENT PRIMARY KEY,
     request_id INT NOT NULL,
                                                transfer_date DATE NOT NULL,
                                                return_date DATE DEFAULT NULL,
                                                notes TEXT,
                                                FOREIGN KEY (request_id) REFERENCES issue_requests(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;