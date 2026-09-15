-- ===================================================================
-- Database Schema for Network Device & IP Address Management System
-- Exactly Five Relational Tables
-- ===================================================================

-- Table 1: Admin
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 2: College
CREATE TABLE IF NOT EXISTS college (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    contact_email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 3: Department (mapped to College)
CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    college_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_department_college FOREIGN KEY (college_id) REFERENCES college(id) ON DELETE CASCADE
);

-- Table 4: Lab (mapped to Department)
CREATE TABLE IF NOT EXISTS lab (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lab_number VARCHAR(50) NOT NULL,
    lab_name VARCHAR(100) NOT NULL,
    location_floor VARCHAR(50),
    total_capacity INT DEFAULT 30,
    department_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lab_department FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE CASCADE
);

-- Table 5: Device (mapped to Lab, includes IP & MAC Management)
CREATE TABLE IF NOT EXISTS device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45) NOT NULL UNIQUE,
    mac_address VARCHAR(25) NOT NULL,
    subnet_mask VARCHAR(45) NOT NULL DEFAULT '255.255.255.0',
    gateway VARCHAR(45) NOT NULL DEFAULT '192.168.1.1',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    lab_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_device_lab FOREIGN KEY (lab_id) REFERENCES lab(id) ON DELETE CASCADE
);
