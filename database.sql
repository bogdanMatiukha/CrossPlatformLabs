DROP DATABASE IF EXISTS university_property_db;

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
    status_item ENUM('AVAILABLE', 'UNAVAILABLE')
    NOT NULL DEFAULT 'AVAILABLE'
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS issue_requests (
     id INT AUTO_INCREMENT PRIMARY KEY,
     user_id INT NOT NULL,
     property_id INT NOT NULL,
     request_status ENUM(
        'CREATED',
        'APPROVED',
        'PUBLISHED',
        'RETURNED',
        'CANCELLED'
) NOT NULL DEFAULT 'CREATED',
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
    transfer_date DATE DEFAULT NULL,
    return_date DATE DEFAULT NULL,
    notes TEXT,
    FOREIGN KEY (request_id) REFERENCES issue_requests(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;

INSERT INTO users (full_name, role, email) VALUES
    ('Іван Петренко', 'Студент', 'i.petrenko@uni.edu'),
    ('Ольга Коваленко', 'Викладач', 'o.kovalenko@uni.edu');

INSERT INTO property_items (inventory_number, name, category, status_item) VALUES
    ('INV-001', 'Проєктор Epson', 'Мультимедіа', 'AVAILABLE'),
    ('INV-002', 'Ноутбук Lenovo ThinkPad', 'Обчислювальна техніка', 'AVAILABLE');

INSERT INTO issue_requests (user_id, property_id, request_status, urgency_f1, criticality_f2, deficit_f3, integral_index) VALUES
   (1, 1, 'CREATED', 80, 70, 50, (0.40*80 + 0.35*70 + 0.25*50));

INSERT INTO transfer_records (request_id, transfer_date, return_date, notes) VALUES
    (1, '2026-10-15', NULL, 'Видано для проведення лабораторної роботи');