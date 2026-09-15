package com.college.networkmgmt.controller.api;

import com.college.networkmgmt.service.DeviceService;
import com.college.networkmgmt.service.IpManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ip")
public class IpRestController {

    private final IpManagementService ipManagementService;
    private final DeviceService deviceService;

    public IpRestController(IpManagementService ipManagementService, DeviceService deviceService) {
        this.ipManagementService = ipManagementService;
        this.deviceService = deviceService;
    }

    @GetMapping("/inspect")
    public ResponseEntity<IpManagementService.IpCheckResult> inspectIp(@RequestParam("ip") String ip) {
        return ResponseEntity.ok(ipManagementService.inspectIp(ip));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateIp(@RequestParam("ip") String ip) {
        boolean valid = ipManagementService.isValidIpv4(ip);
        return ResponseEntity.ok(Map.of(
                "ip", ip,
                "validIpv4", valid
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getIpStats() {
        return ResponseEntity.ok(Map.of(
                "totalAssignedIps", deviceService.countTotal(),
                "activeIps", deviceService.countActive(),
                "inactiveIps", deviceService.countInactive()
        ));
    }
}
