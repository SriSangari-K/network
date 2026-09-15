package com.college.networkmgmt.controller;

import com.college.networkmgmt.model.Device;
import com.college.networkmgmt.service.DeviceService;
import com.college.networkmgmt.service.LabService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final LabService labService;

    private static final List<String> DEVICE_TYPES = Arrays.asList(
            "ROUTER", "SWITCH", "PC", "SERVER", "ACCESS_POINT", "FIREWALL", "PRINTER", "OTHER"
    );

    public DeviceController(DeviceService deviceService, LabService labService) {
        this.deviceService = deviceService;
        this.labService = labService;
    }

    @GetMapping
    public String listDevices(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "deviceType", required = false) String deviceType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "labId", required = false) Long labId,
            Model model
    ) {
        List<Device> devices = deviceService.searchDevices(keyword, deviceType, status, labId);

        model.addAttribute("devices", devices);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedType", deviceType);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedLabId", labId);
        model.addAttribute("deviceTypes", DEVICE_TYPES);
        model.addAttribute("labs", labService.findAll());
        model.addAttribute("activePage", "devices");

        return "devices/list";
    }

    @GetMapping("/view/{id}")
    public String viewDeviceDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Device> deviceOpt = deviceService.findById(id);
        if (deviceOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Device not found with ID: " + id);
            return "redirect:/devices";
        }

        model.addAttribute("device", deviceOpt.get());
        model.addAttribute("activePage", "devices");
        return "devices/view";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes) {
        if (labService.count() == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please create at least one Lab before registering a Network Device.");
            return "redirect:/labs/new";
        }

        Device device = new Device();
        device.setSubnetMask("255.255.255.0");
        device.setGateway("192.168.1.1");
        device.setStatus("ACTIVE");

        model.addAttribute("device", device);
        model.addAttribute("deviceTypes", DEVICE_TYPES);
        model.addAttribute("labs", labService.findAll());
        model.addAttribute("pageTitle", "Add New Network Device");
        model.addAttribute("activePage", "devices");
        return "devices/form";
    }

    @PostMapping("/save")
    public String saveDevice(@Valid @ModelAttribute("device") Device device,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("deviceTypes", DEVICE_TYPES);
            model.addAttribute("labs", labService.findAll());
            model.addAttribute("pageTitle", device.getId() == null ? "Add New Network Device" : "Edit Network Device");
            model.addAttribute("activePage", "devices");
            return "devices/form";
        }

        try {
            deviceService.save(device);
            String action = device.getId() == null ? "added" : "updated";
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Device '" + device.getDeviceName() + "' with IP [" + device.getIpAddress() + "] successfully " + action + ".");
            return "redirect:/devices";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("deviceTypes", DEVICE_TYPES);
            model.addAttribute("labs", labService.findAll());
            model.addAttribute("pageTitle", device.getId() == null ? "Add New Network Device" : "Edit Network Device");
            model.addAttribute("activePage", "devices");
            return "devices/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Device> deviceOpt = deviceService.findById(id);
        if (deviceOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Device not found with ID: " + id);
            return "redirect:/devices";
        }

        model.addAttribute("device", deviceOpt.get());
        model.addAttribute("deviceTypes", DEVICE_TYPES);
        model.addAttribute("labs", labService.findAll());
        model.addAttribute("pageTitle", "Edit Network Device");
        model.addAttribute("activePage", "devices");
        return "devices/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteDevice(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            deviceService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Network device deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete device: " + ex.getMessage());
        }
        return "redirect:/devices";
    }
}
