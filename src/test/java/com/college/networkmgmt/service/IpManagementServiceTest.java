package com.college.networkmgmt.service;

import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.model.Lab;
import com.college.networkmgmt.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpManagementServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private IpManagementService ipManagementService;

    @Test
    @DisplayName("Test Valid IPv4 Addresses")
    void testValidIpv4() {
        assertTrue(ipManagementService.isValidIpv4("192.168.1.1"));
        assertTrue(ipManagementService.isValidIpv4("10.0.0.1"));
        assertTrue(ipManagementService.isValidIpv4("172.16.254.1"));
        assertTrue(ipManagementService.isValidIpv4("255.255.255.255"));
        assertTrue(ipManagementService.isValidIpv4("0.0.0.0"));
        assertTrue(ipManagementService.isValidIpv4(" 192.168.1.50 ")); // with whitespace trim
    }

    @Test
    @DisplayName("Test Invalid IPv4 Addresses")
    void testInvalidIpv4() {
        assertFalse(ipManagementService.isValidIpv4(null));
        assertFalse(ipManagementService.isValidIpv4(""));
        assertFalse(ipManagementService.isValidIpv4("   "));
        assertFalse(ipManagementService.isValidIpv4("256.0.0.1")); // octet > 255
        assertFalse(ipManagementService.isValidIpv4("192.168.1.999")); // octet > 255
        assertFalse(ipManagementService.isValidIpv4("192.168.1")); // missing octet
        assertFalse(ipManagementService.isValidIpv4("192.168.1.1.1")); // too many octets
        assertFalse(ipManagementService.isValidIpv4("abc.def.ghi.jkl")); // non-numeric
        assertFalse(ipManagementService.isValidIpv4("192.168.1.-1")); // negative number
    }

    @Test
    @DisplayName("Test Valid MAC Addresses")
    void testValidMac() {
        assertTrue(ipManagementService.isValidMac("00:1A:2B:3C:4D:5E"));
        assertTrue(ipManagementService.isValidMac("00-1A-2B-3C-4D-5E"));
        assertTrue(ipManagementService.isValidMac("aa:bb:cc:dd:ee:ff"));
        assertTrue(ipManagementService.isValidMac("12:34:56:78:9A:BC"));
    }

    @Test
    @DisplayName("Test Invalid MAC Addresses")
    void testInvalidMac() {
        assertFalse(ipManagementService.isValidMac(null));
        assertFalse(ipManagementService.isValidMac(""));
        assertFalse(ipManagementService.isValidMac("00:1A:2B:3C:4D")); // only 5 pairs
        assertFalse(ipManagementService.isValidMac("00:1A:2B:3C:4D:5E:6F")); // 7 pairs
        assertFalse(ipManagementService.isValidMac("00:1Z:2B:3C:4D:5E")); // non-hex digit 'Z'
        assertFalse(ipManagementService.isValidMac("001A2B3C4D5E")); // no delimiter
    }

    @Test
    @DisplayName("Test Duplicate IP Detection for New Device")
    void testDuplicateIpNewDevice() {
        String testIp = "192.168.1.100";
        when(deviceRepository.existsByIpAddress(testIp)).thenReturn(true);

        boolean duplicate = ipManagementService.isIpDuplicate(testIp, null);
        assertTrue(duplicate);
        verify(deviceRepository).existsByIpAddress(testIp);
    }

    @Test
    @DisplayName("Test Duplicate IP Detection for Existing Device Update")
    void testDuplicateIpExistingDeviceUpdate() {
        String testIp = "192.168.1.100";
        Long currentDeviceId = 5L;
        when(deviceRepository.existsByIpAddressAndIdNot(testIp, currentDeviceId)).thenReturn(false);

        boolean duplicate = ipManagementService.isIpDuplicate(testIp, currentDeviceId);
        assertFalse(duplicate);
        verify(deviceRepository).existsByIpAddressAndIdNot(testIp, currentDeviceId);
    }

    @Test
    @DisplayName("Test Inspect IP when Available")
    void testInspectIpAvailable() {
        String testIp = "192.168.1.55";
        when(deviceRepository.findByIpAddress(testIp)).thenReturn(Optional.empty());

        IpManagementService.IpCheckResult result = ipManagementService.inspectIp(testIp);
        assertTrue(result.validFormat());
        assertFalse(result.duplicate());
        assertEquals(testIp, result.ipAddress());
        assertNull(result.assignedDevice());
    }

    @Test
    @DisplayName("Test Inspect IP when Conflicted")
    void testInspectIpConflicted() {
        String testIp = "192.168.1.1";
        Lab lab = new Lab("LAB-301", "Network Lab", "3rd Floor", 30, null);
        Device existing = new Device("Core-Router", "ROUTER", testIp, "00:11:22:33:44:55",
                "255.255.255.0", "192.168.1.254", "ACTIVE", "Main", lab);

        when(deviceRepository.findByIpAddress(testIp)).thenReturn(Optional.of(existing));

        IpManagementService.IpCheckResult result = ipManagementService.inspectIp(testIp);
        assertTrue(result.validFormat());
        assertTrue(result.duplicate());
        assertNotNull(result.assignedDevice());
        assertEquals("Core-Router", result.assignedDevice().getDeviceName());
    }
}
