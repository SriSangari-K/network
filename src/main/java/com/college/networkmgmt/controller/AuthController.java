package com.college.networkmgmt.controller;

import com.college.networkmgmt.model.Admin;
import com.college.networkmgmt.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final AdminService adminService;

    public AuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            HttpServletRequest request,
            Model model
    ) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedAdmin") != null) {
            return "redirect:/dashboard";
        }

        if ("unauthorized".equals(error)) {
            model.addAttribute("errorMessage", "Session expired or unauthorized. Please log in first.");
        } else if ("invalid".equals(error)) {
            model.addAttribute("errorMessage", "Invalid administrator username or password.");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "You have successfully logged out.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Admin> adminOpt = adminService.authenticate(username, password);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            HttpSession session = request.getSession(true);
            session.setAttribute("loggedAdmin", admin);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + admin.getFullName() + "!");
            return "redirect:/dashboard";
        } else {
            return "redirect:/login?error=invalid";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=true";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpServletRequest request) {
        return logout(request);
    }
}
