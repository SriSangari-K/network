# Network Device and IP Address Management System (ND-IPAMS)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2021%20%7C%2024-orange.svg)](https://www.oracle.com/java/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20H2-blue.svg)](https://www.mysql.com/)
[![Frontend](https://img.shields.io/badge/Frontend-HTML%20%2B%20CSS%20%2B%20Bootstrap%20(No%20JS)-purple.svg)](#frontend-architecture)
[![License](https://img.shields.io/badge/Project-Academic%202nd%20Year-success.svg)](#)

A complete, production-grade **Network Device and IP Address Management System (ND-IPAMS)** built for an Engineering College. The project provides centralized tracking of campus network equipment (Routers, Switches, PCs, Servers, Access Points, Firewalls) across colleges, departments, and laboratories, while strictly managing IPv4 allocations, format validations, and duplicate IP collision prevention.

---

## 📌 Project Highlights

- **Strict No-JavaScript Architecture**: Pure HTML5 and Bootstrap 5 CSS. Form submissions, validations, searches, and deletions are handled purely via standard HTML forms and server-side redirects (Zero `<script>` tags, zero AJAX).
- **Exact Five-Table Schema**: Clean relational database model consisting of exactly five tables (`admin`, `college`, `department`, `lab`, `device`) with cascading relationships.
- **Controller → Service → Repository Pattern**: Strict separation of concerns following industry-standard Spring Boot architecture.
- **Dual Interface**:
  - **Server-Rendered Web UI**: Thymeleaf templates powered by Bootstrap 5.
  - **REST API Backend**: Complete JSON REST API endpoints under `/api/...` for third-party integration and Postman testing.
- **IP Address Management**:
  - Validates IPv4 format (ensuring 4 decimal octets strictly within 0–255).
  - Enforces duplicate IP rejection across all campus network nodes.
  - Standalone IP Availability & Conflict Audit utility.
  - Hardware MAC address format validation (`00:1A:2B:3C:4D:5E`).
- **Secure Admin Authentication**: BCrypt password hashing and session-based route interception.
- **Out-of-the-Box Runnability**: Supports MySQL by default, with an instant H2 in-memory profile for evaluation on machines without MySQL.

---

## 🏗️ Relational Architecture (5 Tables)

```
+-----------------------------------------------------------------------------------+
|                                  COLLEGE                                          |
|  id (PK), name, code (UK), contact_email, phone, address, created_at              |
+-----------------------------------------+-----------------------------------------+
                                          | 1
                                          |
                                          | N
+-----------------------------------------v-----------------------------------------+
|                                DEPARTMENT                                         |
|  id (PK), name, code, college_id (FK), created_at                                 |
+-----------------------------------------+-----------------------------------------+
                                          | 1
                                          |
                                          | N
+-----------------------------------------v-----------------------------------------+
|                                   LAB                                             |
|  id (PK), lab_number, lab_name, location_floor, total_capacity,                   |
|  department_id (FK), created_at                                                   |
+-----------------------------------------+-----------------------------------------+
                                          | 1
                                          |
                                          | N
+-----------------------------------------v-----------------------------------------+
|                                  DEVICE                                           |
|  id (PK), device_name, device_type, ip_address (UK), mac_address,                 |
|  subnet_mask, gateway, status, notes, lab_id (FK), created_at                     |
+-----------------------------------------------------------------------------------+

+-----------------------------------------------------------------------------------+
|                                   ADMIN                                           |
|  id (PK), username (UK), password (BCrypt), full_name, email, created_at          |
+-----------------------------------------------------------------------------------+
```

---

## 💻 Tech Stack

| Component | Technology | Description |
|---|---|---|
| **Backend Framework** | Spring Boot 3.3.3 | MVC, Dependency Injection, Auto-configuration |
| **Persistence / ORM** | Spring Data JPA / Hibernate | Object-Relational Mapping with MySQL/H2 Dialects |
| **Database** | MySQL 8.x (Default) / H2 (Fallback) | Relational Database storage |
| **Security** | BCrypt / Servlet Session Filter | Password hashing and protected route interception |
| **Frontend UI** | HTML5, CSS3, Bootstrap 5 | Pure markup and styling (**Zero JavaScript**) |
| **Template Engine** | Thymeleaf | Server-side template rendering |
| **Build Tool** | Apache Maven / Maven Wrapper | Automated dependency and lifecycle management |

---

## 🚀 Quick Start & Run Instructions

### Option A: Instant Run with H2 (No MySQL Setup Required)
Ideal for testing immediately without installing or configuring MySQL:

```bash
# Using Maven:
mvn spring-boot:run -Dspring-boot.run.profiles=h2

# Or using Maven Wrapper (Unix/Mac):
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# Or using Maven Wrapper (Windows):
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```
*H2 web console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:college_network_db`, User: `sa`, Password: empty).*

---

### Option B: Run with MySQL (Production Setup)

#### Step 1: Start MySQL and Create Database
Open MySQL Workbench or MySQL CLI:
```sql
CREATE DATABASE IF NOT EXISTS college_network_db;
```

#### Step 2: Configure Database Credentials
Edit `src/main/resources/application.properties` if your MySQL username or password differs from `root`/`root`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/college_network_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

#### Step 3: Run the Application
```bash
mvn spring-boot:run
```

*Note: Hibernate (`spring.jpa.hibernate.ddl-auto=update`) automatically creates the 5 tables upon first launch, and the built-in `DataInitializer` seeds demonstration data.*

---

## 🔑 Default Login Credentials

| Role | Username | Password |
|---|---|---|
| **System Administrator** | `admin` | `admin123` |

Access the web portal at: **[http://localhost:8080](http://localhost:8080)**

---

## 🖥️ Application Modules & Features

### 1. Secure Authentication & Session Interception
- Admin login with BCrypt password hashing.
- Route interceptor (`AuthInterceptor`) ensures all management screens require an active admin session.
- Secure session invalidation on logout.

### 2. Dashboard (`/dashboard`)
- Real-time counts: **Total Colleges**, **Departments**, **Laboratories**, and **Network Devices**.
- **Active vs. Inactive status metrics**: Percentage breakdown and visual status indicators.
- **Recent Device Registrations**: Direct links to newly added network equipment.

### 3. College Management (`/colleges`)
- View list of colleges with code, name, contact email, and campus address.
- Create new college with unique college code validation.
- Edit and delete college (with cascade protection for sub-departments).

### 4. Department Management (`/departments`)
- Add/View/Edit/Delete departments mapped to specific colleges.
- Filter departments by parent college via native HTML GET form.

### 5. Laboratory Management (`/labs`)
- Add/View/Edit/Delete laboratories mapped to academic departments.
- Tracks lab room number, physical floor, and total student node capacity.
- Filter labs by department.

### 6. Device Management (`/devices`)
- **Search & Filter**: Search by device name, type, IP address, MAC address, status, and laboratory.
- **Add / Edit Device**:
  - Name, Type (Router, Switch, PC, Server, Access Point, Firewall, Printer, Other).
  - Assigned IPv4 address with strict format validation.
  - Hardware MAC address validation.
  - Subnet Mask and Default Gateway.
  - Operational Status (`ACTIVE` or `INACTIVE`).
  - Physical Lab assignment.
- **Device Details Profile (`/devices/view/{id}`)**: Full specifications, network parameters, and hierarchical placement breadcrumb.

### 7. IP Address Management (`/ip-management`)
- Central IP address allocation registry.
- **IPv4 Format Validator**: Checks format against IPv4 standard octet bounds (0-255).
- **Duplicate IP Conflict Checker**: Audits any given IP against database records and reports which device currently holds the IP.

---

## 📡 REST API Reference

All REST endpoints return JSON format and can be tested with cURL or Postman:

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/colleges` | List all colleges |
| `GET` | `/api/colleges/{id}` | Get college by ID |
| `POST` | `/api/colleges` | Create a new college |
| `PUT` | `/api/colleges/{id}` | Update existing college |
| `DELETE` | `/api/colleges/{id}` | Delete college |
| `GET` | `/api/departments` | List all departments (`?collegeId={id}`) |
| `POST` | `/api/departments` | Create a new department |
| `GET` | `/api/labs` | List all labs (`?departmentId={id}`) |
| `POST` | `/api/labs` | Create a new lab |
| `GET` | `/api/devices` | Search devices (`?keyword=&deviceType=&status=&labId=`) |
| `GET` | `/api/devices/{id}` | Get device by ID |
| `GET` | `/api/devices/ip/{ip}` | Get device by IP address |
| `POST` | `/api/devices` | Register device & assign IP (checks duplicates) |
| `PUT` | `/api/devices/{id}` | Update device & IP |
| `DELETE` | `/api/devices/{id}` | Remove device |
| `GET` | `/api/ip/inspect?ip={ip}` | Check IP format and conflict status |
| `GET` | `/api/ip/stats` | IP assignment statistics |

### Sample cURL Commands

```bash
# 1. Inspect an IP address availability
curl -X GET "http://localhost:8080/api/ip/inspect?ip=192.168.1.1"

# 2. Search devices by keyword
curl -X GET "http://localhost:8080/api/devices?keyword=Cisco"

# 3. Get network statistics
curl -X GET "http://localhost:8080/api/devices/stats"
```

---

## 🧪 Automated Testing

Execute the test suite using Maven:
```bash
mvn test
```
The test suite includes:
1. `IpManagementServiceTest`: Validates IPv4 regex, octet boundary conditions (0-255), MAC address formats, and duplicate IP collision queries.
2. `DeviceServiceTest`: Verifies device saving, duplicate IP rejection, and invalid input exceptions.
3. `NetworkManagementApplicationTests`: Verifies full Spring Boot application context initialization with H2 in-memory profile.

---

## 📂 Project Directory Structure

```
network-mgmt-system/
├── pom.xml                               # Maven project dependencies & plugins
├── mvnw / mvnw.cmd                       # Maven Wrapper scripts
├── README.md                             # Comprehensive documentation
├── src/
│   ├── main/
│   │   ├── java/com/college/networkmgmt/
│   │   │   ├── NetworkManagementApplication.java  # Main entrypoint
│   │   │   ├── config/
│   │   │   │   ├── AuthInterceptor.java           # Session auth protection
│   │   │   │   ├── WebMvcConfig.java              # Interceptor & resource registry
│   │   │   │   └── DataInitializer.java           # DB startup seeder
│   │   │   ├── model/                             # EXACTLY 5 JPA Entities
│   │   │   │   ├── Admin.java                     # Table 1: admin
│   │   │   │   ├── College.java                   # Table 2: college
│   │   │   │   ├── Department.java                # Table 3: department
│   │   │   │   ├── Lab.java                       # Table 4: lab
│   │   │   │   └── Device.java                    # Table 5: device
│   │   │   ├── repository/                        # Spring Data Repositories
│   │   │   │   ├── AdminRepository.java
│   │   │   │   ├── CollegeRepository.java
│   │   │   │   ├── DepartmentRepository.java
│   │   │   │   ├── LabRepository.java
│   │   │   │   └── DeviceRepository.java
│   │   │   ├── service/                           # Business Logic & Validations
│   │   │   │   ├── AdminService.java
│   │   │   │   ├── CollegeService.java
│   │   │   │   ├── DepartmentService.java
│   │   │   │   ├── LabService.java
│   │   │   │   ├── DeviceService.java
│   │   │   │   └── IpManagementService.java       # IP format & collision engine
│   │   │   └── controller/                        # Controllers
│   │   │       ├── AuthController.java            # Web: Login & Logout
│   │   │       ├── DashboardController.java       # Web: Overview & metrics
│   │   │       ├── CollegeController.java         # Web: College CRUD
│   │   │       ├── DepartmentController.java      # Web: Department CRUD
│   │   │       ├── LabController.java             # Web: Lab CRUD
│   │   │       ├── DeviceController.java          # Web: Device CRUD & Search
│   │   │       ├── IpManagementWebController.java # Web: IP checker & matrix
│   │   │       └── api/                           # REST API Endpoints
│   │   │           ├── CollegeRestController.java
│   │   │           ├── DepartmentRestController.java
│   │   │           ├── LabRestController.java
│   │   │           ├── DeviceRestController.java
│   │   │           └── IpRestController.java
│   │   └── resources/
│   │       ├── application.properties             # Primary MySQL config
│   │       ├── application-h2.properties          # In-memory test config
│   │       ├── schema.sql                         # Raw SQL DDL (5 tables)
│   │       ├── data.sql                           # Initial SQL seed data
│   │       ├── static/css/
│   │       │   └── style.css                      # Custom styling & offline fallbacks
│   │       └── templates/                         # HTML Templates (NO JAVASCRIPT)
│   │           ├── login.html                     # Secure login screen
│   │           ├── dashboard.html                 # Metrics & status breakdown
│   │           ├── fragments/layout.html          # Navbar, alerts, footer
│   │           ├── colleges/
│   │           │   ├── list.html                  # College directory
│   │           │   └── form.html                  # Add/edit college
│   │           ├── departments/
│   │           │   ├── list.html                  # Department directory
│   │           │   └── form.html                  # Add/edit department
│   │           ├── labs/
│   │           │   ├── list.html                  # Lab directory
│   │           │   └── form.html                  # Add/edit lab
│   │           ├── devices/
│   │           │   ├── list.html                  # Search & device table
│   │           │   ├── form.html                  # Device register/edit
│   │           │   └── view.html                  # Device network profile
│   │           └── ip/
│   │               └── overview.html              # IP collision checker
│   └── test/java/com/college/networkmgmt/
│       ├── NetworkManagementApplicationTests.java # Context loading test
│       └── service/
│           ├── IpManagementServiceTest.java       # IP & MAC validation unit tests
│           └── DeviceServiceTest.java             # Device validation unit tests
```

---

## 🎓 Academic Viva & Presentation Notes

When presenting this project:
1. **Explain the 5-table hierarchy**: `College` has many `Departments`, `Department` has many `Labs`, `Lab` has many `Devices`. `Admin` is isolated for secure credentials.
2. **Explain duplicate IP checking**: Handled at two levels:
   - **Database level**: Unique constraint on `device.ip_address`.
   - **Application level**: `IpManagementService.isIpDuplicate()` prevents collisions with user-friendly error banners before DB constraint violation.
3. **Explain the No-JavaScript design**: Demonstrate that search filters use `<form method="get">`, deletes use `<form method="post">`, and input validations use native HTML5 regex patterns (`pattern="..."`), making the site fast, accessible, lightweight, and resilient.
