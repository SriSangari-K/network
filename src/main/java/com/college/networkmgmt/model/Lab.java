package com.college.networkmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Table 4: Lab Entity
 * Represents a Computer, IoT, or Networking Laboratory within an Academic Department.
 */
@Entity
@Table(name = "lab")
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Lab number/identifier is required")
    @Size(max = 50, message = "Lab number cannot exceed 50 characters")
    @Column(name = "lab_number", nullable = false, length = 50)
    private String labNumber;

    @NotBlank(message = "Lab name is required")
    @Size(max = 100, message = "Lab name cannot exceed 100 characters")
    @Column(name = "lab_name", nullable = false, length = 100)
    private String labName;

    @Column(name = "location_floor", length = 50)
    private String locationFloor;

    @Min(value = 1, message = "Capacity must be at least 1 workstation")
    @Column(name = "total_capacity")
    private Integer totalCapacity = 30;

    @NotNull(message = "Department mapping is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Device> devices = new ArrayList<>();

    public Lab() {
        this.createdAt = LocalDateTime.now();
    }

    public Lab(String labNumber, String labName, String locationFloor, Integer totalCapacity, Department department) {
        this();
        this.labNumber = labNumber;
        this.labName = labName;
        this.locationFloor = locationFloor;
        this.totalCapacity = totalCapacity;
        this.department = department;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabNumber() {
        return labNumber;
    }

    public void setLabNumber(String labNumber) {
        this.labNumber = labNumber;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLocationFloor() {
        return locationFloor;
    }

    public void setLocationFloor(String locationFloor) {
        this.locationFloor = locationFloor;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
