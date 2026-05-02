// controller/HomeController.java
package com.hospital.controller;

import com.hospital.service.ArticleService;
import com.hospital.service.DepartmentService;
import com.hospital.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final ArticleService articleService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("articles", articleService.getLatestArticles());
        return "views/index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "views/departments";
    }

    @GetMapping("/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "views/doctors";
    }
}