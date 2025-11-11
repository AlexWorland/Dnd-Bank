package com.dndbank.bank.controller;

import com.dndbank.bank.entity.User;
import com.dndbank.bank.enums.Role;
import com.dndbank.bank.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/dashboard"})
    public String redirectDashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        if (user.getRole() == Role.DM) {
            return "redirect:/dm/dashboard";
        }
        return "redirect:/player/dashboard";
    }
}
