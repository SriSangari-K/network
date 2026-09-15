package com.college.networkmgmt.service;

import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.model.Lab;
import com.college.networkmgmt.repository.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private IpManagementService ipManagementService;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    @DisplayName("Should successfully save a valid network device")
    void testSaveValidDevice() {
        Lab lab = new Lab();
        lab.setId(1L);

        Device device = new Device("Lab-Switch-01", "SWITCH", "192.168.1.10", "00:1A:2B:3C:4D:5E",
                "255.255.255.0", "192.168.1.1", "ACTIVE", "Testing", lab);

        when(ipManagementService.isValidIpv4("192.168.1.10")).thenReturn(true);
        when(ipManagementService.isIpDuplicate("192.168.1.10", null)).thenReturn(false);
        when(ipManagementService.isValidMac("00:1A:2B:3C:4D:5E")).thenReturn(true);
        when(ipManagementService.isValidIpv4("255.255.255.0")).thenReturn(true);
        when(ipManagementService.isValidIpv4("192.168.1.1")).thenReturn(true);
        when(deviceRepository.save(any(Device.class))).thenAnswer(i -> {
            Device d = i.getArgument(0);
            d.setId(10L);
            return d;
        });

        Device saved = deviceService.save(device);
        assertNotNull(saved.getId());
        assertEquals("192.168.1.10", saved.getIpAddress());
        verify(deviceRepository).save(device);
    }

    @Test
    @DisplayName("Should reject device with invalid IP address format")
    void testSaveDeviceInvalidIp() {
        Lab lab = new Lab();
        lab.setId(1L);

        Device device = new Device("Bad-Device", "PC", "999.999.999.999", "00:1A:2B:3C:4D:5E",
                "255.255.255.0", "192.168.1.1", "ACTIVE", "Testing", lab);

        when(ipManagementService.isValidIpv4("999.999.999.999")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> deviceService.save(device));
        assertTrue(ex.getMessage().contains("Invalid IPv4 address format"));
        verify(deviceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject device with duplicate IP address")
    void testSaveDeviceDuplicateIp() {
        Lab lab = new Lab();
        lab.setId(1L);

        Device device = new Device("Duplicate-Device", "PC", "192.168.1.1", "00:1A:2B:3C:4D:5E",
                "255.255.255.0", "192.168.1.1", "ACTIVE", "Testing", lab);

        when(ipManagementService.isValidIpv4("192.168.1.1")).thenReturn(true);
        when(ipManagementService.isIpDuplicate("192.168.1.1", null)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> deviceService.save(device));
        assertTrue(ex.getMessage().contains("IP Address Conflict"));
        verify(deviceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject device with invalid MAC address")
    void testSaveDeviceInvalidMac() {
        Lab lab = new Lab();
        lab.setId(1L);

        Device device = new Device("Bad-Mac-Device", "PC", "192.168.1.50", "INVALID-MAC",
                "255.255.255.0", "192.168.1.1", "ACTIVE", "Testing", lab);

        when(ipManagementService.isValidIpv4("192.168.1.50")).thenReturn(true);
        when(ipManagementService.isIpDuplicate("192.168.1.50", null)).thenReturn(false);
        when(ipManagementService.isValidMac("INVALID-MAC")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> deviceService.save(device));
        assertTrue(ex.getMessage().contains("Invalid MAC address format"));
        verify(deviceRepository, never()).save(any());
    }
}
