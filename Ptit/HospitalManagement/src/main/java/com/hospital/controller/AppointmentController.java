// controller/AppointmentController.java
package com.hospital.controller;

import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.Appointment;
import com.hospital.entity.Department;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.service.AppointmentService;
import com.hospital.service.DepartmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    @GetMapping("/book")
    public String showBookForm(@RequestParam(required = false) Integer step,
                               @RequestParam(required = false) Long deptId,
                               @RequestParam(required = false) Long doctorId,
                               @RequestParam(required = false) LocalDate date,
                               @RequestParam(required = false) String slot,
                               Model model,
                               HttpSession session) {

        if (session.getAttribute("userPhone") == null) {
            return "redirect:/views/login";
        }

        if (step == null) step = 1;
        model.addAttribute("step", step);

        if (step == 1) {
            model.addAttribute("departments", departmentService.getAllDepartments());
        }
        if (step == 2 && deptId != null) {
            model.addAttribute("doctors", doctorService.getDoctorsByDepartment(deptId));
            model.addAttribute("deptId", deptId);
        }
        if (step == 3 && doctorId != null) {
            model.addAttribute("doctorId", doctorId);
            model.addAttribute("deptId", deptId);
        }
        if (step == 4) {
            model.addAttribute("doctorId", doctorId);
            model.addAttribute("deptId", deptId);
            model.addAttribute("selectedDate", date);
            model.addAttribute("selectedSlot", slot);
        }
        return "views/book";
    }

    @PostMapping("/book/submit")
    public String submitBooking(@Valid @ModelAttribute AppointmentDTO appointmentDTO,
                                BindingResult result,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/book?step=4";
        }

        // Check slot availability
        if (!appointmentService.isSlotAvailable(appointmentDTO.getDoctorId(),
                appointmentDTO.getAppointmentDate(), appointmentDTO.getSlot())) {
            ra.addFlashAttribute("error", "Ca khám đã đầy, vui lòng chọn ca khác");
            return "redirect:/book?step=3&doctorId=" + appointmentDTO.getDoctorId();
        }

        // Check if user is logged in and phone matches
        String loggedPhone = (String) session.getAttribute("userPhone");
        User user = null;
        if (loggedPhone != null && loggedPhone.equals(appointmentDTO.getPatientPhone())) {
            Optional<User> userOpt = userService.findByPhone(loggedPhone);
            if (userOpt.isPresent()) user = userOpt.get();
        }

        Doctor doctor = doctorService.getDoctorById(appointmentDTO.getDoctorId());
        Department department = departmentService.getDepartmentById(appointmentDTO.getDepartmentId());

        Appointment appointment = Appointment.builder()
                .patientName(appointmentDTO.getPatientName())
                .patientPhone(appointmentDTO.getPatientPhone())
                .patientEmail(appointmentDTO.getPatientEmail())
                .patientDob(appointmentDTO.getPatientDob())
                .patientGender(appointmentDTO.getPatientGender())
                .patientAddress(appointmentDTO.getPatientAddress())
                .identityNo(appointmentDTO.getIdentityNo())
                .insuranceNo(appointmentDTO.getInsuranceNo())
                .doctor(doctor)
                .department(department)
                .appointmentDate(appointmentDTO.getAppointmentDate())
                .slot(appointmentDTO.getSlot())
                .symptoms(appointmentDTO.getSymptoms())
                .user(user)
                .build();

        Appointment saved = appointmentService.createAppointment(appointment);
        if (saved != null) {
            ra.addFlashAttribute("message", "Đặt lịch thành công! Mã lịch hẹn: " + saved.getAppointmentCode());
            return "redirect:/search-appointment";
        } else {
            ra.addFlashAttribute("error", "Đặt lịch thất bại");
            return "redirect:/book?step=4";
        }
    }
}