package com.college.networkmgmt.controller;

import com.college.networkmgmt.model.Department;
import com.college.networkmgmt.service.CollegeService;
import com.college.networkmgmt.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final CollegeService collegeService;

    public DepartmentController(DepartmentService departmentService, CollegeService collegeService) {
        this.departmentService = departmentService;
        this.collegeService = collegeService;
    }

    @GetMapping
    public String listDepartments(
            @RequestParam(value = "collegeId", required = false) Long collegeId,
            Model model
    ) {
        if (collegeId != null) {
            model.addAttribute("departments", departmentService.findByCollegeId(collegeId));
            model.addAttribute("selectedCollegeId", collegeId);
        } else {
            model.addAttribute("departments", departmentService.findAll());
        }
        model.addAttribute("colleges", collegeService.findAll());
        model.addAttribute("activePage", "departments");
        return "departments/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes) {
        if (collegeService.count() == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please create at least one College before adding a Department.");
            return "redirect:/colleges/new";
        }

        model.addAttribute("department", new Department());
        model.addAttribute("colleges", collegeService.findAll());
        model.addAttribute("pageTitle", "Add New Department");
        model.addAttribute("activePage", "departments");
        return "departments/form";
    }

    @PostMapping("/save")
    public String saveDepartment(@Valid @ModelAttribute("department") Department department,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("colleges", collegeService.findAll());
            model.addAttribute("pageTitle", department.getId() == null ? "Add New Department" : "Edit Department");
            model.addAttribute("activePage", "departments");
            return "departments/form";
        }

        try {
            departmentService.save(department);
            String action = department.getId() == null ? "created" : "updated";
            redirectAttributes.addFlashAttribute("successMessage", "Department '" + department.getName() + "' successfully " + action + ".");
            return "redirect:/departments";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("colleges", collegeService.findAll());
            model.addAttribute("pageTitle", department.getId() == null ? "Add New Department" : "Edit Department");
            model.addAttribute("activePage", "departments");
            return "departments/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Department> deptOpt = departmentService.findById(id);
        if (deptOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department not found with ID: " + id);
            return "redirect:/departments";
        }

        model.addAttribute("department", deptOpt.get());
        model.addAttribute("colleges", collegeService.findAll());
        model.addAttribute("pageTitle", "Edit Department");
        model.addAttribute("activePage", "departments");
        return "departments/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department and its associated labs/devices deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete department: " + ex.getMessage());
        }
        return "redirect:/departments";
    }
}
