-- ===================================================================
-- Initial Seed Data for College Network Management System
-- Default Admin credentials: admin / admin123 (BCrypt hashed)
-- ===================================================================

-- 1. Admin Seed (Password: admin123)
-- BCrypt hash for 'admin123': $2a$10$w8uM4m7DqA1bFvO5mHjLXe1S0yH0k2E6zKjT3uK9nL2Q1xY8v9.w2
-- For standard initial setup:
INSERT INTO admin (id, username, password, full_name, email, created_at)
SELECT 1, 'admin', '$2a$10$w8uM4m7DqA1bFvO5mHjLXe1S0yH0k2E6zKjT3uK9nL2Q1xY8v9.w2', 'Network System Administrator', 'admin@college.edu', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin');

-- 2. College Seed
INSERT INTO college (id, name, code, contact_email, phone, address, created_at)
SELECT 1, 'National Institute of Engineering & Technology', 'NIET-001', 'info@niet.edu', '+1-555-0199', '100 Campus Avenue, University Tech Park', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM college WHERE code = 'NIET-001');

-- 3. Department Seed
INSERT INTO department (id, name, code, college_id, created_at)
SELECT 1, 'Computer Science & Engineering', 'CSE', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM department WHERE id = 1);

INSERT INTO department (id, name, code, college_id, created_at)
SELECT 2, 'Information Technology', 'IT', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM department WHERE id = 2);

INSERT INTO department (id, name, code, college_id, created_at)
SELECT 3, 'Electronics & Communication', 'ECE', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM department WHERE id = 3);

-- 4. Lab Seed
INSERT INTO lab (id, lab_number, lab_name, location_floor, total_capacity, department_id, created_at)
SELECT 1, 'LAB-301', 'Advanced Networking & Security Lab', '3rd Floor - Main Block', 40, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM lab WHERE id = 1);

INSERT INTO lab (id, lab_number, lab_name, location_floor, total_capacity, department_id, created_at)
SELECT 2, 'LAB-204', 'Cloud Computing & Systems Lab', '2nd Floor - IT Wing', 35, 2, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM lab WHERE id = 2);

INSERT INTO lab (id, lab_number, lab_name, location_floor, total_capacity, department_id, created_at)
SELECT 3, 'LAB-102', 'IoT & Embedded Systems Lab', '1st Floor - Hardware Wing', 30, 3, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM lab WHERE id = 3);

-- 5. Device Seed (with IP & MAC Addresses)
INSERT INTO device (id, device_name, device_type, ip_address, mac_address, subnet_mask, gateway, status, notes, lab_id, created_at)
SELECT 1, 'Core-Gateway-Router', 'ROUTER', '192.168.1.1', '00:1A:2B:3C:4D:5E', '255.255.255.0', '192.168.1.254', 'ACTIVE', 'Primary Cisco Catalyst Core Router for CSE Labs', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE ip_address = '192.168.1.1');

INSERT INTO device (id, device_name, device_type, ip_address, mac_address, subnet_mask, gateway, status, notes, lab_id, created_at)
SELECT 2, 'CSE-Dist-Switch-01', 'SWITCH', '192.168.1.2', '00:1A:2B:3C:4D:5F', '255.255.255.0', '192.168.1.1', 'ACTIVE', '24-Port Managed Gigabit PoE Switch', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE ip_address = '192.168.1.2');

INSERT INTO device (id, device_name, device_type, ip_address, mac_address, subnet_mask, gateway, status, notes, lab_id, created_at)
SELECT 3, 'Firewall-Appliance-01', 'FIREWALL', '192.168.1.5', 'AA:BB:CC:11:22:33', '255.255.255.0', '192.168.1.1', 'ACTIVE', 'Fortinet Network Security Gateway', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE ip_address = '192.168.1.5');

INSERT INTO device (id, device_name, device_type, ip_address, mac_address, subnet_mask, gateway, status, notes, lab_id, created_at)
SELECT 4, 'Lab301-Workstation-01', 'PC', '192.168.1.101', 'BC:24:11:99:88:77', '255.255.255.0', '192.168.1.1', 'ACTIVE', 'Dell OptiPlex Core i7 Workstation', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE ip_address = '192.168.1.101');

INSERT INTO device (id, device_name, device_type, ip_address, mac_address, subnet_mask, gateway, status, notes, lab_id, created_at)
SELECT 5, 'Lab301-Workstation-02', 'PC', '192.168.1.102', 'BC:24:11:99:88:78', '255.255.255.0', '192.168.1.1', 'ACTIVE', 'Dell OptiPlex Core i7 Workstation', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE ip_address = '192.168.1.102');

INSERT INTO device (id, device_name, device_type, ip_address, mac_address, subnet_mask, gateway, status, notes, lab_id, created_at)
SELECT 6, 'Cloud-Server-Node-01', 'SERVER', '192.168.2.10', 'E4:F8:9C:12:34:56', '255.255.255.0', '192.168.2.1', 'ACTIVE', 'Dell PowerEdge R740 virtualization host', 2, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE ip_address = '192.168.2.10');

INSERT INTO device (id, device_name, device_type, ip_address, mac_address, subnet_mask, gateway, status, notes, lab_id, created_at)
SELECT 7, 'IoT-Wireless-AP-01', 'ACCESS_POINT', '192.168.3.20', 'F0:9F:C2:55:66:77', '255.255.255.0', '192.168.3.1', 'INACTIVE', 'Ubiquiti UniFi AP 6 Pro - scheduled for maintenance', 3, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM device WHERE ip_address = '192.168.3.20');
