package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.repository.*;
import com.hospital.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PatientController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // ==================== DASHBOARD (HOME PAGE) ====================
    @GetMapping("/patient/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Get featured doctors (first 6)
        List<Doctor> featuredDoctors = doctorRepository.findByStatus(Doctor.Status.active)
                .stream().limit(6).toList();

        List<Specialty> specialties = specialtyRepository.findByStatus(Specialty.Status.active);

        model.addAttribute("featuredDoctors", featuredDoctors);
        model.addAttribute("specialties", specialties);

        return "patient/dashboard";
    }

    // ==================== ABOUT ====================
    @GetMapping("/about")
    public String about() {
        return "patient/about";
    }

    // ==================== DEPARTMENTS (SPECIALTIES) ====================
    @GetMapping("/departments")
    public String departments(Model model) {
        List<Specialty> specialties = specialtyRepository.findByStatus(Specialty.Status.active);
        model.addAttribute("specialties", specialties);
        return "patient/departments";
    }

    // ==================== DOCTORS ====================
    @GetMapping("/doctors")
    public String doctors(Model model,
                          @RequestParam(required = false) Long specialtyId) {
        List<Specialty> specialties = specialtyRepository.findByStatus(Specialty.Status.active);
        List<Doctor> doctors;

        if (specialtyId != null && specialtyId > 0) {
            Specialty specialty = specialtyRepository.findById(specialtyId).orElse(null);
            doctors = doctorRepository.findBySpecialtyAndStatus(specialty, Doctor.Status.active);
            model.addAttribute("selectedSpecialtyId", specialtyId);
        } else {
            doctors = doctorRepository.findByStatus(Doctor.Status.active);
        }

        model.addAttribute("specialties", specialties);
        model.addAttribute("doctors", doctors);
        return "patient/doctors";
    }

    @GetMapping("/books")
    public String showBookingForm(Model model) {
        List<Doctor> doctors = doctorRepository.findByStatus(Doctor.Status.active);
        List<Shift> shifts = shiftRepository.findAll();
        List<Specialty> specialties = specialtyRepository.findByStatus(Specialty.Status.active); // THÊM DÒNG NÀY

        model.addAttribute("doctors", doctors);
        model.addAttribute("shifts", shifts);
        model.addAttribute("specialties", specialties); // THÊM DÒNG NÀY
        model.addAttribute("selectedDate", LocalDate.now().plusDays(1));

        return "patient/books";
    }

    @PostMapping("/books")
    public String processBooking(@RequestParam Long doctorId,
                                 @RequestParam Long shiftId,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate appointmentDate,
                                 @RequestParam(required = false) String reason,
                                 @RequestParam(required = false) String patientNote,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Long patientId = (Long) session.getAttribute("userId");

        if (patientId == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đặt lịch khám");
            return "redirect:/login";
        }

        try {
            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            Shift shift = shiftRepository.findById(shiftId).orElse(null);

            if (doctor == null || shift == null) {
                throw new RuntimeException("Dữ liệu không hợp lệ");
            }

            // Calculate appointment time based on shift
            LocalTime appointmentTime = shift.getStartTime();

            Appointment appointment = appointmentService.createAppointment(
                    patientId, doctorId, shiftId, appointmentDate, appointmentTime, reason, patientNote
            );

            redirectAttributes.addFlashAttribute("success",
                    "Đặt lịch thành công! Mã đặt lịch: " + appointment.getBookingCode());

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/appointments";
    }

    // ==================== MY APPOINTMENTS ====================
    @GetMapping("/appointments")
    public String myAppointments(Model model, HttpSession session) {
        Long patientId = (Long) session.getAttribute("userId");

        if (patientId == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
        model.addAttribute("appointments", appointments);

        return "patient/appointments";
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Appointment cancelled = appointmentService.cancelAppointment(id);

        if (cancelled != null) {
            redirectAttributes.addFlashAttribute("success", "Đã hủy lịch hẹn thành công");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy lịch hẹn này");
        }

        return "redirect:/appointments";
    }

    // ==================== MEDICAL HISTORY ====================
    @GetMapping("/history")
    public String medicalHistory(Model model, HttpSession session) {
        Long patientId = (Long) session.getAttribute("userId");

        if (patientId == null) {
            return "redirect:/login";
        }

        List<Appointment> completedAppointments = appointmentRepository.findByPatient(
                        userRepository.findById(patientId).orElse(null)
                ).stream()
                .filter(a -> a.getStatus() == Appointment.Status.completed)
                .toList();

        model.addAttribute("appointments", completedAppointments);
        return "patient/history";
    }

    // ==================== PROFILE ====================
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);

        return "patient/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        User existingUser = userRepository.findById(userId).orElse(null);

        if (existingUser != null) {
            existingUser.setFullName(updatedUser.getFullName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setProvince(updatedUser.getProvince());
            existingUser.setDistrict(updatedUser.getDistrict());
            existingUser.setWard(updatedUser.getWard());
            existingUser.setStreetAddress(updatedUser.getStreetAddress());
            existingUser.setInsuranceNumber(updatedUser.getInsuranceNumber());

            userRepository.save(existingUser);

            // Update session
            session.setAttribute("userName", existingUser.getFullName());
            session.setAttribute("userPhone", existingUser.getPhone());
            session.setAttribute("userEmail", existingUser.getEmail());

            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        }

        return "redirect:/profile";
    }

    @GetMapping("/api/appointments/doctor/{doctorId}/date/{date}")
    @ResponseBody
    public List<Map<String, Object>> getBookedAppointments(@PathVariable Long doctorId, @PathVariable String date) {
        LocalDate appointmentDate = LocalDate.parse(date);
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        List<Appointment> appointments = appointmentRepository.findByDoctorAndAppointmentDate(doctor, appointmentDate);

        return appointments.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("shiftId", a.getShift().getId());
            map.put("time", a.getAppointmentTime().toString());
            return map;
        }).collect(Collectors.toList());
    }
}