package com.college.networkmgmt.controller.api;

import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.service.DeviceService;
import com.college.networkmgmt.service.LabService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class DeviceRestController {

    private final DeviceService deviceService;
    private final LabService labService;

    public DeviceRestController(DeviceService deviceService, LabService labService) {
        this.deviceService = deviceService;
        this.labService = labService;
    }

    @GetMapping
    public List<Device> searchOrListDevices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labId
    ) {
        return deviceService.searchDevices(keyword, deviceType, status, labId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeviceById(@PathVariable Long id) {
        return deviceService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Device not found with id: " + id)));
    }

    @GetMapping("/ip/{ipAddress}")
    public ResponseEntity<?> getDeviceByIp(@PathVariable String ipAddress) {
        return deviceService.findByIpAddress(ipAddress)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Device not found with IP address: " + ipAddress)));
    }

    @GetMapping("/stats")
    public Map<String, Object> getDeviceStatistics() {
        return Map.of(
                "totalDevices", deviceService.countTotal(),
                "activeDevices", deviceService.countActive(),
                "inactiveDevices", deviceService.countInactive()
        );
    }

    @PostMapping
    public ResponseEntity<?> createDevice(@Valid @RequestBody Device device) {
        if (device.getLab() == null || device.getLab().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lab id is required for device assignment"));
        }
        if (labService.findById(device.getLab().getId()).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lab does not exist with specified id"));
        }
        try {
            Device created = deviceService.save(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @Valid @RequestBody Device device) {
        if (deviceService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Device not found with id: " + id));
        }
        device.setId(id);
        try {
            Device updated = deviceService.save(device);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        if (deviceService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Device not found with id: " + id));
        }
        deviceService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Device deleted successfully"));
    }
}
