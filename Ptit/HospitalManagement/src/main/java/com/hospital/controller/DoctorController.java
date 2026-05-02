package com.hospital.controller;

import com.hospital.entity.Appointment;
import com.hospital.entity.MedicalRecord;
import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.service.AppointmentService;
import com.hospital.service.MedicalRecordService;
import com.hospital.service.UserService;
import com.hospital.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final UserService userService;
    private final DoctorService doctorService;

    private boolean isDoctor(HttpSession session) {
        return "DOCTOR".equals(session.getAttribute("role"));
    }

    private Long getCurrentDoctorId(HttpSession session) {
        String phone = (String) session.getAttribute("userPhone");
        if (phone == null) return null;

        // Tìm user theo phone
        Optional<User> userOpt = userService.findByPhone(phone);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Tìm doctor theo userId (giả sử Doctor có trường userId)
            // Nếu không có, bạn có thể query trực tiếp hoặc bỏ qua
            List<Doctor> doctors = doctorService.getAllDoctors();
            for (Doctor d : doctors) {
                // So sánh theo tên hoặc theo userId tùy cấu trúc
                if (d.getName().equals(user.getFullname())) {
                    return d.getId();
                }
            }
        }
        return null;
    }

    @GetMapping("/patients")
    public String patientList(HttpSession session, Model model) {
        if (!isDoctor(session)) return "redirect:/views/login";

        Long doctorId = getCurrentDoctorId(session);
        if (doctorId == null) {
            model.addAttribute("error", "Không tìm thấy thông tin bác sĩ");
            return "doctor/patients";
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        model.addAttribute("appointments", appointments);
        return "doctor/patients";
    }

    @GetMapping("/examine/{id}")
    public String examineForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isDoctor(session)) return "redirect:/views/login";

        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            model.addAttribute("error", "Không tìm thấy lịch hẹn");
            return "doctor/patients";
        }
        model.addAttribute("appointment", appointment);
        return "doctor/examine";
    }

    @PostMapping("/examine/{id}")
    public String examineSubmit(@PathVariable Long id,
                                @RequestParam String diagnosis,
                                @RequestParam(required = false) String prescription,
                                @RequestParam(required = false) String notes,
                                RedirectAttributes ra) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            if (appointment == null) {
                ra.addFlashAttribute("error", "Không tìm thấy lịch hẹn");
                return "redirect:/doctor/patients";
            }

            appointmentService.updateStatus(id, "COMPLETED");

            MedicalRecord record = MedicalRecord.builder()
                    .appointment(appointment)
                    .diagnosis(diagnosis)
                    .prescription(prescription)
                    .notes(notes)
                    .build();
            medicalRecordService.saveMedicalRecord(record);

            ra.addFlashAttribute("message", "Đã cập nhật kết quả khám thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Cập nhật thất bại: " + e.getMessage());
        }
        return "redirect:/doctor/patients";
    }

    @GetMapping("/schedule")
    public String schedule(HttpSession session, Model model) {
        if (!isDoctor(session)) return "redirect:/views/login";

        Long doctorId = getCurrentDoctorId(session);
        if (doctorId == null) {
            model.addAttribute("error", "Không tìm thấy thông tin bác sĩ");
            return "doctor/schedule";
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        model.addAttribute("appointments", appointments);
        return "doctor/schedule";
    }

    @GetMapping("/medical-records")
    public String medicalRecords(HttpSession session, Model model) {
        if (!isDoctor(session)) return "redirect:/views/login";

        Long doctorId = getCurrentDoctorId(session);
        List<Appointment> appointments = appointmentService.getCompletedAppointmentsByDoctorId(doctorId);

        // Tạo map để lưu medical_record theo appointment_id
        Map<Long, MedicalRecord> recordMap = new HashMap<>();
        for (Appointment app : appointments) {
            MedicalRecord record = medicalRecordService.getByAppointmentId((long) app.getId()).orElse(null);
            if (record != null) {
                recordMap.put((long) app.getId(), record);
            }
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("recordMap", recordMap);
        return "doctor/medical-records";
    }
}