package com.college.networkmgmt.controller;

import com.college.networkmgmt.model.Lab;
import com.college.networkmgmt.service.DepartmentService;
import com.college.networkmgmt.service.LabService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/labs")
public class LabController {

    private final LabService labService;
    private final DepartmentService departmentService;

    public LabController(LabService labService, DepartmentService departmentService) {
        this.labService = labService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String listLabs(
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            Model model
    ) {
        if (departmentId != null) {
            model.addAttribute("labs", labService.findByDepartmentId(departmentId));
            model.addAttribute("selectedDepartmentId", departmentId);
        } else {
            model.addAttribute("labs", labService.findAll());
        }
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("activePage", "labs");
        return "labs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes) {
        if (departmentService.count() == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please create at least one Department before adding a Lab.");
            return "redirect:/departments/new";
        }

        model.addAttribute("lab", new Lab());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("pageTitle", "Add New Lab");
        model.addAttribute("activePage", "labs");
        return "labs/form";
    }

    @PostMapping("/save")
    public String saveLab(@Valid @ModelAttribute("lab") Lab lab,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("pageTitle", lab.getId() == null ? "Add New Lab" : "Edit Lab");
            model.addAttribute("activePage", "labs");
            return "labs/form";
        }

        try {
            labService.save(lab);
            String action = lab.getId() == null ? "created" : "updated";
            redirectAttributes.addFlashAttribute("successMessage", "Lab '" + lab.getLabName() + " (" + lab.getLabNumber() + ")' successfully " + action + ".");
            return "redirect:/labs";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("pageTitle", lab.getId() == null ? "Add New Lab" : "Edit Lab");
            model.addAttribute("activePage", "labs");
            return "labs/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Lab> labOpt = labService.findById(id);
        if (labOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lab not found with ID: " + id);
            return "redirect:/labs";
        }

        model.addAttribute("lab", labOpt.get());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("pageTitle", "Edit Lab");
        model.addAttribute("activePage", "labs");
        return "labs/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteLab(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            labService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Lab and its associated devices deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete lab: " + ex.getMessage());
        }
        return "redirect:/labs";
    }
}
