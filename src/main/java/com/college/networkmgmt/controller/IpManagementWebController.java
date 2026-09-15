package com.college.networkmgmt.controller;

import com.college.networkmgmt.service.DeviceService;
import com.college.networkmgmt.service.IpManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ip-management")
public class IpManagementWebController {

    private final IpManagementService ipManagementService;
    private final DeviceService deviceService;

    public IpManagementWebController(IpManagementService ipManagementService, DeviceService deviceService) {
        this.ipManagementService = ipManagementService;
        this.deviceService = deviceService;
    }

    @GetMapping
    public String showIpOverview(Model model) {
        long totalAssigned = deviceService.countTotal();
        long activeCount = deviceService.countActive();
        long inactiveCount = deviceService.countInactive();

        model.addAttribute("devices", deviceService.findAll());
        model.addAttribute("totalAssigned", totalAssigned);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("inactiveCount", inactiveCount);
        model.addAttribute("activePage", "ip-management");

        return "ip/overview";
    }

    @PostMapping("/check")
    public String checkIpAvailability(
            @RequestParam("checkIp") String checkIp,
            RedirectAttributes redirectAttributes
    ) {
        IpManagementService.IpCheckResult result = ipManagementService.inspectIp(checkIp);
        redirectAttributes.addFlashAttribute("ipCheckResult", result);
        redirectAttributes.addFlashAttribute("searchedIp", checkIp);
        return "redirect:/ip-management";
    }
}
