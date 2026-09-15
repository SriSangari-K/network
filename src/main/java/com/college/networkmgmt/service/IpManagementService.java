package com.college.networkmgmt.service;

import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service dedicated to Network IP Address and MAC Address Management.
 * Enforces strict IPv4 format validation, range checks, and duplicate detection.
 */
@Service
@Transactional(readOnly = true)
public class IpManagementService {

    private final DeviceRepository deviceRepository;

    // Strict IPv4 regex ensuring octets 0-255
    private static final String IPV4_REGEX = 
            "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])$";
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    // MAC address pattern (supports colon or hyphen separators)
    private static final String MAC_REGEX = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
    private static final Pattern MAC_PATTERN = Pattern.compile(MAC_REGEX);

    public IpManagementService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Validates whether a given string is a valid IPv4 address.
     */
    public boolean isValidIpv4(String ip) {
        if (ip == null || ip.isBlank()) {
            return false;
        }
        String trimmed = ip.trim();
        if (!IPV4_PATTERN.matcher(trimmed).matches()) {
            return false;
        }

        // Extra verification of octet bounds (0-255)
        String[] octets = trimmed.split("\\.");
        if (octets.length != 4) {
            return false;
        }
        for (String octet : octets) {
            try {
                int val = Integer.parseInt(octet);
                if (val < 0 || val > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates whether a given string is a valid MAC address.
     */
    public boolean isValidMac(String mac) {
        if (mac == null || mac.isBlank()) {
            return false;
        }
        return MAC_PATTERN.matcher(mac.trim()).matches();
    }

    /**
     * Checks if an IP address is already registered to any device.
     * If currentDeviceId is provided (update flow), that device is excluded from collision check.
     */
    public boolean isIpDuplicate(String ip, Long currentDeviceId) {
        if (ip == null || ip.isBlank()) {
            return false;
        }
        String cleanIp = ip.trim();
        if (currentDeviceId == null) {
            return deviceRepository.existsByIpAddress(cleanIp);
        } else {
            return deviceRepository.existsByIpAddressAndIdNot(cleanIp, currentDeviceId);
        }
    }

    /**
     * Detailed result object for standalone IP collision & status checking.
     */
    public record IpCheckResult(
            boolean validFormat,
            boolean duplicate,
            String ipAddress,
            String message,
            Device assignedDevice
    ) {}

    /**
     * Performs a comprehensive check of an IP address.
     */
    public IpCheckResult inspectIp(String ip) {
        if (!isValidIpv4(ip)) {
            return new IpCheckResult(
                    false,
                    false,
                    ip,
                    "Invalid IPv4 format. IP must consist of 4 octets between 0 and 255 (e.g., 192.168.1.50).",
                    null
            );
        }

        String cleanIp = ip.trim();
        Optional<Device> existing = deviceRepository.findByIpAddress(cleanIp);
        if (existing.isPresent()) {
            Device dev = existing.get();
            String msg = String.format("IP '%s' is CONFLICTED / IN USE by Device: '%s' (%s) in Lab: '%s' [%s].",
                    cleanIp, dev.getDeviceName(), dev.getDeviceType(), dev.getLab().getLabName(), dev.getStatus());
            return new IpCheckResult(true, true, cleanIp, msg, dev);
        } else {
            return new IpCheckResult(true, false, cleanIp, "IP address '" + cleanIp + "' is AVAILABLE for assignment.", null);
        }
    }
}
