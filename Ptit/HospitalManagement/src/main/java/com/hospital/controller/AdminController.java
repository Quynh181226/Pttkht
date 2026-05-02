package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final InvoiceService invoiceService;
    private final UserService userService;

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return role != null && role.equals("ADMIN");
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/views/login";
        model.addAttribute("totalAppointments", appointmentService.getAllAppointments().size());
        model.addAttribute("totalDepartments", departmentService.getAllDepartments().size());
        model.addAttribute("totalDoctors", doctorService.getAllDoctors().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        return "admin/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/views/login";
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "admin/appointments";
    }

    @PostMapping("/appointment/update-status")
    public String updateStatus(@RequestParam Long id, @RequestParam String status, RedirectAttributes ra) {
        if (appointmentService.updateStatus(id, status)) {
            ra.addFlashAttribute("message", "Cập nhật trạng thái thành công");
        } else {
            ra.addFlashAttribute("error", "Cập nhật thất bại");
        }
        return "redirect:/admin/appointments";
    }

    @GetMapping("/revenue")
    public String revenue(HttpSession session, Model model,
                          @RequestParam(required = false) Long deptId,
                          @RequestParam(required = false) LocalDate from,
                          @RequestParam(required = false) LocalDate to) {
        if (!isAdmin(session)) return "redirect:/views/login";

        model.addAttribute("departments", departmentService.getAllDepartments());

        if (deptId != null && from != null && to != null) {
            List<Invoice> invoices = invoiceService.getRevenueByDepartment(deptId, from, to);
            model.addAttribute("invoices", invoices);
            model.addAttribute("deptId", deptId);
            model.addAttribute("from", from);
            model.addAttribute("to", to);
        }
        return "admin/revenue";
    }

    @GetMapping("/departments")
    public String manageDepartments(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/views/login";
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/departments";
    }

    @PostMapping("/department/add")
    public String addDepartment(@RequestParam String name, @RequestParam String description, RedirectAttributes ra) {
        Department dept = Department.builder()
                .name(name)
                .description(description)
                .build();
        departmentService.saveDepartment(dept);
        ra.addFlashAttribute("message", "Thêm khoa thành công");
        return "redirect:/admin/departments";
    }

    @PostMapping("/department/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes ra) {
        departmentService.deleteDepartment(id);
        ra.addFlashAttribute("message", "Xóa khoa thành công");
        return "redirect:/admin/departments";
    }

    @GetMapping("/doctors")
    public String manageDoctors(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/views/login";
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/doctors";
    }

    @PostMapping("/doctor/add")
    public String addDoctor(@RequestParam String name, @RequestParam Long departmentId,
                            @RequestParam String qualification, @RequestParam Integer experience,
                            @RequestParam BigDecimal fee, RedirectAttributes ra) {
        Department dept = departmentService.getDepartmentById(departmentId);
        Doctor doctor = Doctor.builder()
                .name(name)
                .department(dept)
                .qualification(qualification)
                .experience(experience)
                .fee(fee)
                .build();
        doctorService.saveDoctor(doctor);
        ra.addFlashAttribute("message", "Thêm bác sĩ thành công");
        return "redirect:/admin/doctors";
    }

    @PostMapping("/doctor/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes ra) {
        doctorService.deleteDoctor(id);
        ra.addFlashAttribute("message", "Xóa bác sĩ thành công");
        return "redirect:/admin/doctors";
    }

    @GetMapping("/patients")
    public String managePatients(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/views/login";
        List<User> patients = userService.getAllPatients();
        model.addAttribute("patients", patients);
        return "admin/patients";
    }

    @PostMapping("/patient/delete/{id}")
    public String deletePatient(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("message", "Xóa bệnh nhân thành công");
        return "redirect:/admin/patients";
    }

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model,
                          @RequestParam(required = false) LocalDate from,
                          @RequestParam(required = false) LocalDate to) {
        if (!isAdmin(session)) return "redirect:/views/login";
        if (from == null) from = LocalDate.now().minusMonths(1);
        if (to == null) to = LocalDate.now();

        BigDecimal totalRevenue = invoiceService.getTotalRevenueByDateRange(from, to);
        long totalAppointments = appointmentService.countByDateRange(from, to);
        long totalPatients = appointmentService.countDistinctPatientsByDateRange(from, to);

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "admin/reports";
    }
}