package com.hospital.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String home(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null) {
            return "redirect:/login";
        }

        if ("admin".equals(role)) {
            return "redirect:/admin/dashboard";
        } else if ("doctor".equals(role)) {
            return "redirect:/doctor/dashboard";
        }
        return "redirect:/patient/dashboard";
    }
}