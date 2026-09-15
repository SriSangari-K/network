package com.college.networkmgmt.controller;

import com.college.networkmgmt.model.College;
import com.college.networkmgmt.service.CollegeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @GetMapping
    public String listColleges(Model model) {
        model.addAttribute("colleges", collegeService.findAll());
        model.addAttribute("activePage", "colleges");
        return "colleges/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("college", new College());
        model.addAttribute("pageTitle", "Add New College");
        model.addAttribute("activePage", "colleges");
        return "colleges/form";
    }

    @PostMapping("/save")
    public String saveCollege(@Valid @ModelAttribute("college") College college,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", college.getId() == null ? "Add New College" : "Edit College");
            model.addAttribute("activePage", "colleges");
            return "colleges/form";
        }

        try {
            collegeService.save(college);
            String action = college.getId() == null ? "created" : "updated";
            redirectAttributes.addFlashAttribute("successMessage", "College '" + college.getName() + "' successfully " + action + ".");
            return "redirect:/colleges";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("pageTitle", college.getId() == null ? "Add New College" : "Edit College");
            model.addAttribute("activePage", "colleges");
            return "colleges/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<College> collegeOpt = collegeService.findById(id);
        if (collegeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "College not found with ID: " + id);
            return "redirect:/colleges";
        }

        model.addAttribute("college", collegeOpt.get());
        model.addAttribute("pageTitle", "Edit College");
        model.addAttribute("activePage", "colleges");
        return "colleges/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteCollege(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            collegeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "College and its associated departments/labs/devices deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete college: " + ex.getMessage());
        }
        return "redirect:/colleges";
    }
}
