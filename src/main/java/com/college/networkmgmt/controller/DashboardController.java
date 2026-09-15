package com.college.networkmgmt.controller;

import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.service.CollegeService;
import com.college.networkmgmt.service.DepartmentService;
import com.college.networkmgmt.service.DeviceService;
import com.college.networkmgmt.service.LabService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final CollegeService collegeService;
    private final DepartmentService departmentService;
    private final LabService labService;
    private final DeviceService deviceService;

    public DashboardController(CollegeService collegeService,
                               DepartmentService departmentService,
                               LabService labService,
                               DeviceService deviceService) {
        this.collegeService = collegeService;
        this.departmentService = departmentService;
        this.labService = labService;
        this.deviceService = deviceService;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        long collegeCount = collegeService.count();
        long departmentCount = departmentService.count();
        long labCount = labService.count();
        long totalDevices = deviceService.countTotal();
        long activeDevices = deviceService.countActive();
        long inactiveDevices = deviceService.countInactive();

        double activePercentage = (totalDevices > 0) ? ((double) activeDevices / totalDevices) * 100.0 : 0.0;
        double inactivePercentage = (totalDevices > 0) ? ((double) inactiveDevices / totalDevices) * 100.0 : 0.0;

        List<Device> recentDevices = deviceService.findRecentDevices();

        model.addAttribute("collegeCount", collegeCount);
        model.addAttribute("departmentCount", departmentCount);
        model.addAttribute("labCount", labCount);
        model.addAttribute("totalDevices", totalDevices);
        model.addAttribute("activeDevices", activeDevices);
        model.addAttribute("inactiveDevices", inactiveDevices);
        model.addAttribute("activePercentage", String.format("%.1f", activePercentage));
        model.addAttribute("inactivePercentage", String.format("%.1f", inactivePercentage));
        model.addAttribute("recentDevices", recentDevices);
        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }
}
