package com.college.networkmgmt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Table 5: Device Entity
 * Represents network hardware (Routers, Switches, PCs, Servers, APs) and IP assignment.
 */
@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Device name is required")
    @Size(max = 100, message = "Device name cannot exceed 100 characters")
    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    @NotBlank(message = "Device type is required")
    @Column(name = "device_type", nullable = false, length = 50)
    private String deviceType; // ROUTER, SWITCH, PC, SERVER, ACCESS_POINT, FIREWALL

    @NotBlank(message = "IP address is required")
    @Pattern(
        regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
        message = "Invalid IPv4 address format (e.g. 192.168.1.100 with octets between 0 and 255)"
    )
    @Column(name = "ip_address", nullable = false, unique = true, length = 45)
    private String ipAddress;

    @NotBlank(message = "MAC address is required")
    @Pattern(
        regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",
        message = "Invalid MAC address format (e.g. 00:1A:2B:3C:4D:5E or 00-1A-2B-3C-4D-5E)"
    )
    @Column(name = "mac_address", nullable = false, length = 25)
    private String macAddress;

    @Pattern(
        regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
        message = "Invalid Subnet Mask format (e.g. 255.255.255.0)"
    )
    @Column(name = "subnet_mask", nullable = false, length = 45)
    private String subnetMask = "255.255.255.0";

    @Pattern(
        regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
        message = "Invalid Gateway IP format (e.g. 192.168.1.1)"
    )
    @Column(nullable = false, length = 45)
    private String gateway = "192.168.1.1";

    @NotBlank(message = "Status is required")
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(columnDefinition = "TEXT")
    private String notes;

    @NotNull(message = "Lab mapping is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lab_id", nullable = false)
    private Lab lab;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Device() {
        this.createdAt = LocalDateTime.now();
    }

    public Device(String deviceName, String deviceType, String ipAddress, String macAddress,
                  String subnetMask, String gateway, String status, String notes, Lab lab) {
        this();
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.subnetMask = (subnetMask != null && !subnetMask.isBlank()) ? subnetMask : "255.255.255.0";
        this.gateway = (gateway != null && !gateway.isBlank()) ? gateway : "192.168.1.1";
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "ACTIVE";
        this.notes = notes;
        this.lab = lab;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status != null) {
            this.status = this.status.toUpperCase();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.status != null) {
            this.status = this.status.toUpperCase();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType != null ? deviceType.toUpperCase() : null;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress != null ? ipAddress.trim() : null;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress != null ? macAddress.trim().toUpperCase() : null;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask != null ? subnetMask.trim() : "255.255.255.0";
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway != null ? gateway.trim() : "192.168.1.1";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim().toUpperCase() : "ACTIVE";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
