package com.example.lostandfound.controller;

import com.example.lostandfound.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/admin")

public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Get the currently authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = principal instanceof User ? ((User) principal).getUsername() : "anonymous";

        // Get statistics for the dashboard
        long totalMembers = adminService.getTotalMembers();
        long totalLostItems = adminService.getTotalLostItems();
        long totalFoundItems = adminService.getTotalFoundItems();

        // Add attributes to the model
        model.addAttribute("currentUser", new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getPassword() {
                return "";
            }

            @Override
            public String getUsername() {
                return "";
            }
        });
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("totalLostItems", totalLostItems);
        model.addAttribute("totalFoundItems", totalFoundItems);

        return "dashboard";
    }
}
