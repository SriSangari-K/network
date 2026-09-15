package com.college.networkmgmt.config;

import com.college.networkmgmt.model.Admin;
import com.college.networkmgmt.model.College;
import com.college.networkmgmt.model.Department;
import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.model.Lab;
import com.college.networkmgmt.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final LabRepository labRepository;
    private final DeviceRepository deviceRepository;

    public DataInitializer(AdminRepository adminRepository,
                           CollegeRepository collegeRepository,
                           DepartmentRepository departmentRepository,
                           LabRepository labRepository,
                           DeviceRepository deviceRepository) {
        this.adminRepository = adminRepository;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
        this.labRepository = labRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking initial system dataset...");

        // 1. Ensure default Admin exists
        if (adminRepository.count() == 0) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Admin admin = new Admin(
                    "admin",
                    encoder.encode("admin123"),
                    "Network Administrator",
                    "admin@college.edu"
            );
            adminRepository.save(admin);
            log.info("Default Admin created: username='admin', password='admin123'");
        }

        // 2. Ensure initial College, Department, Lab, and Devices exist
        if (collegeRepository.count() == 0) {
            College college = new College(
                    "National Institute of Engineering & Technology",
                    "NIET-001",
                    "info@niet.edu",
                    "+1-555-0199",
                    "100 Campus Avenue, University Tech Park"
            );
            college = collegeRepository.save(college);

            // Departments
            Department cseDept = departmentRepository.save(new Department("Computer Science & Engineering", "CSE", college));
            Department itDept = departmentRepository.save(new Department("Information Technology", "IT", college));
            Department eceDept = departmentRepository.save(new Department("Electronics & Communication", "ECE", college));

            // Labs
            Lab cseLab1 = labRepository.save(new Lab("LAB-301", "Advanced Networking & Systems Lab", "3rd Floor - Main Block", 40, cseDept));
            Lab cseLab2 = labRepository.save(new Lab("LAB-302", "Artificial Intelligence & Cloud Lab", "3rd Floor - Main Block", 35, cseDept));
            Lab itLab1 = labRepository.save(new Lab("LAB-204", "Web Technologies & Systems Lab", "2nd Floor - IT Wing", 35, itDept));
            Lab eceLab1 = labRepository.save(new Lab("LAB-102", "IoT & Embedded Networks Lab", "1st Floor - Hardware Wing", 30, eceDept));

            // Network Devices & Assigned IPs
            deviceRepository.save(new Device(
                    "Core-Gateway-Router", "ROUTER", "192.168.1.1", "00:1A:2B:3C:4D:5E",
                    "255.255.255.0", "192.168.1.254", "ACTIVE",
                    "Primary Cisco Catalyst 8300 Router for College Core Gateway", cseLab1
            ));

            deviceRepository.save(new Device(
                    "CSE-Dist-Switch-01", "SWITCH", "192.168.1.2", "00:1A:2B:3C:4D:5F",
                    "255.255.255.0", "192.168.1.1", "ACTIVE",
                    "48-Port Managed Gigabit PoE Switch for Rack 1", cseLab1
            ));

            deviceRepository.save(new Device(
                    "Security-Firewall-01", "FIREWALL", "192.168.1.5", "AA:BB:CC:11:22:33",
                    "255.255.255.0", "192.168.1.1", "ACTIVE",
                    "Fortinet FortiGate 60F UTM Firewall Appliance", cseLab1
            ));

            deviceRepository.save(new Device(
                    "CSE-Lab301-PC01", "PC", "192.168.1.101", "BC:24:11:99:88:77",
                    "255.255.255.0", "192.168.1.1", "ACTIVE",
                    "Student Workstation - Dell OptiPlex Core i7, 16GB RAM", cseLab1
            ));

            deviceRepository.save(new Device(
                    "CSE-Lab301-PC02", "PC", "192.168.1.102", "BC:24:11:99:88:78",
                    "255.255.255.0", "192.168.1.1", "ACTIVE",
                    "Student Workstation - Dell OptiPlex Core i7, 16GB RAM", cseLab1
            ));

            deviceRepository.save(new Device(
                    "AI-GPU-Compute-Server", "SERVER", "192.168.1.200", "E4:F8:9C:12:34:56",
                    "255.255.255.0", "192.168.1.1", "ACTIVE",
                    "Dell PowerEdge R750 with 2x NVIDIA RTX A6000", cseLab2
            ));

            deviceRepository.save(new Device(
                    "IT-Web-Server-01", "SERVER", "192.168.2.10", "D8:5E:D3:44:55:66",
                    "255.255.255.0", "192.168.2.1", "ACTIVE",
                    "Internal Web Development & Apache Testing Server", itLab1
            ));

            deviceRepository.save(new Device(
                    "IoT-Wireless-AP-01", "ACCESS_POINT", "192.168.3.20", "F0:9F:C2:55:66:77",
                    "255.255.255.0", "192.168.3.1", "INACTIVE",
                    "Ubiquiti UniFi AP 6 Pro - scheduled for firmware upgrade", eceLab1
            ));

            log.info("Preloaded initial demonstration data: 1 College, 3 Departments, 4 Labs, 8 Devices.");
        }
    }
}
