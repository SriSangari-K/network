package com.college.networkmgmt.service;

import com.college.networkmgmt.model.Admin;
import com.college.networkmgmt.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * Authenticates an admin using username and plain text password.
     */
    @Transactional(readOnly = true)
    public Optional<Admin> authenticate(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return Optional.empty();
        }

        Optional<Admin> adminOpt = adminRepository.findByUsername(username.trim());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // Verify BCrypt hashed password or legacy plain text fallback for initial seeds
            if (passwordEncoder.matches(rawPassword, admin.getPassword()) || rawPassword.equals(admin.getPassword())) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }

    /**
     * Registers a new administrator with BCrypt password hashing.
     */
    public Admin registerAdmin(String username, String rawPassword, String fullName, String email) {
        if (adminRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        if (adminRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' is already registered.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        Admin admin = new Admin(username.trim(), hashedPassword, fullName.trim(), email.trim());
        return adminRepository.save(admin);
    }

    /**
     * Finds an admin by username.
     */
    @Transactional(readOnly = true)
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    /**
     * Finds an admin by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
