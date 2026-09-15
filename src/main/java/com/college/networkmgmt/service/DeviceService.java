package com.college.networkmgmt.service;

import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final IpManagementService ipManagementService;

    public DeviceService(DeviceRepository deviceRepository, IpManagementService ipManagementService) {
        this.deviceRepository = deviceRepository;
        this.ipManagementService = ipManagementService;
    }

    @Transactional(readOnly = true)
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Device> findByIpAddress(String ipAddress) {
        return deviceRepository.findByIpAddress(ipAddress);
    }

    @Transactional(readOnly = true)
    public List<Device> findByLabId(Long labId) {
        return deviceRepository.findByLabId(labId);
    }

    public Device save(Device device) {
        // 1. Format & Clean inputs
        if (device.getIpAddress() != null) {
            device.setIpAddress(device.getIpAddress().trim());
        }
        if (device.getMacAddress() != null) {
            device.setMacAddress(device.getMacAddress().trim().toUpperCase());
        }
        if (device.getStatus() != null) {
            device.setStatus(device.getStatus().trim().toUpperCase());
        }
        if (device.getDeviceType() != null) {
            device.setDeviceType(device.getDeviceType().trim().toUpperCase());
        }

        // 2. Validate IP Address Format
        if (!ipManagementService.isValidIpv4(device.getIpAddress())) {
            throw new IllegalArgumentException("Invalid IPv4 address format: '" + device.getIpAddress() + 
                    "'. Must consist of 4 decimal octets (0-255) separated by dots.");
        }

        // 3. Check for Duplicate IP Address across the network
        if (ipManagementService.isIpDuplicate(device.getIpAddress(), device.getId())) {
            throw new IllegalArgumentException("IP Address Conflict: '" + device.getIpAddress() + 
                    "' is already assigned to another network device.");
        }

        // 4. Validate MAC Address Format
        if (!ipManagementService.isValidMac(device.getMacAddress())) {
            throw new IllegalArgumentException("Invalid MAC address format: '" + device.getMacAddress() + 
                    "'. Standard formats: 00:1A:2B:3C:4D:5E or 00-1A-2B-3C-4D-5E.");
        }

        // 5. Validate Subnet Mask and Gateway if provided
        if (device.getSubnetMask() != null && !device.getSubnetMask().isBlank()) {
            if (!ipManagementService.isValidIpv4(device.getSubnetMask())) {
                throw new IllegalArgumentException("Invalid Subnet Mask format: '" + device.getSubnetMask() + "'.");
            }
        } else {
            device.setSubnetMask("255.255.255.0");
        }

        if (device.getGateway() != null && !device.getGateway().isBlank()) {
            if (!ipManagementService.isValidIpv4(device.getGateway())) {
                throw new IllegalArgumentException("Invalid Gateway IP format: '" + device.getGateway() + "'.");
            }
        } else {
            device.setGateway("192.168.1.1");
        }

        // 6. Ensure Lab association exists
        if (device.getLab() == null || device.getLab().getId() == null) {
            throw new IllegalArgumentException("A valid lab must be selected for this network device.");
        }

        return deviceRepository.save(device);
    }

    public void deleteById(Long id) {
        deviceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Device> searchDevices(String keyword, String deviceType, String status, Long labId) {
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String cleanType = (deviceType != null && !deviceType.isBlank()) ? deviceType.trim().toUpperCase() : null;
        String cleanStatus = (status != null && !status.isBlank()) ? status.trim().toUpperCase() : null;

        return deviceRepository.searchDevices(cleanKeyword, cleanType, cleanStatus, labId);
    }

    @Transactional(readOnly = true)
    public long countTotal() {
        return deviceRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return deviceRepository.countByStatus("ACTIVE");
    }

    @Transactional(readOnly = true)
    public long countInactive() {
        return deviceRepository.countByStatus("INACTIVE");
    }

    @Transactional(readOnly = true)
    public List<Device> findRecentDevices() {
        return deviceRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
