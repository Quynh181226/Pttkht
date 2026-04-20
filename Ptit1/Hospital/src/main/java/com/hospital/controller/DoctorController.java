package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.repository.*;
import com.hospital.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository; // THÊM

    private Doctor getCurrentDoctor(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return null;
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        return doctorRepository.findByUser(user).orElse(null);
    }

    private boolean isDoctor(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "doctor".equals(role);
    }

    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isDoctor(session)) return "redirect:/login";

        Doctor doctor = getCurrentDoctor(session);
        if (doctor == null) return "redirect:/login";

        List<Appointment> todayAppointments = appointmentRepository.findByDoctorAndAppointmentDate(doctor, LocalDate.now());

        long pendingCount = todayAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.pending).count();
        long completedCount = todayAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.completed).count();

        model.addAttribute("doctor", doctor);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);

        return "doctor/dashboard";
    }

    // ========== DANH SÁCH CHỜ KHÁM ==========
    @GetMapping("/waiting-list")
    public String waitingList(Model model, HttpSession session,
                              @RequestParam(defaultValue = "0") int page) {
        if (!isDoctor(session)) return "redirect:/login";

        Doctor doctor = getCurrentDoctor(session);
        Pageable pageable = PageRequest.of(page, 10);
        Page<Appointment> pendingPage = appointmentRepository.findByDoctorAndAppointmentDateAndStatus(
                doctor, LocalDate.now(), Appointment.Status.pending, pageable);

        model.addAttribute("appointments", pendingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pendingPage.getTotalPages());
        return "doctor/waiting-list";
    }

    // ========== FORM KHÁM BỆNH ==========
    @GetMapping("/examine/{appointmentId}")
    public String examinationForm(@PathVariable Long appointmentId, Model model, HttpSession session) {
        if (!isDoctor(session)) return "redirect:/login";

        Appointment appointment = appointmentService.findById(appointmentId);
        if (appointment == null) return "redirect:/doctor/waiting-list";

        List<Medicine> medicines = medicineRepository.findByStatus(Medicine.Status.active);

        model.addAttribute("appointment", appointment);
        model.addAttribute("medicines", medicines);
        return "doctor/examine";
    }

    // ========== LƯU BỆNH ÁN & ĐƠN THUỐC ==========
    @PostMapping("/examine/save")
    public String saveExamination(@RequestParam Long appointmentId,
                                  @RequestParam String symptoms,
                                  @RequestParam String diagnosis,
                                  @RequestParam(required = false) String conclusion,
                                  @RequestParam(required = false) List<Long> medicineIds,
                                  @RequestParam(required = false) List<Integer> quantities,
                                  @RequestParam(required = false) List<String> dosages,
                                  @RequestParam(required = false) String instruction, // THÊM instruction
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        if (!isDoctor(session)) return "redirect:/login";

        try {
            Appointment appointment = appointmentService.findById(appointmentId);
            if (appointment == null) {
                throw new RuntimeException("Không tìm thấy lịch hẹn");
            }

            // 1. Tạo medical record
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setRecordCode("MR" + System.currentTimeMillis());
            medicalRecord.setAppointment(appointment);
            medicalRecord.setSymptoms(symptoms);
            medicalRecord.setDiagnosis(diagnosis);
            medicalRecord.setConclusion(conclusion);
            medicalRecordRepository.save(medicalRecord);

            // 2. Tạo đơn thuốc nếu có thuốc
            if (medicineIds != null && !medicineIds.isEmpty() && quantities != null) {
                Prescription prescription = new Prescription();
                prescription.setPrescriptionCode("RX" + System.currentTimeMillis());
                prescription.setMedicalRecord(medicalRecord);
                prescription.setStatus(Prescription.Status.pending);
                prescription.setNote(instruction); // lưu hướng dẫn chung
                prescriptionRepository.save(prescription);

                // 3. Lưu chi tiết đơn thuốc
                for (int i = 0; i < medicineIds.size(); i++) {
                    Medicine medicine = medicineRepository.findById(medicineIds.get(i)).orElse(null);
                    if (medicine != null && i < quantities.size()) {
                        PrescriptionDetail detail = new PrescriptionDetail();
                        detail.setPrescription(prescription);
                        detail.setMedicine(medicine);
                        detail.setQuantity(quantities.get(i));
                        if (dosages != null && i < dosages.size()) {
                            detail.setDosage(dosages.get(i));
                        }
                        detail.setInstruction(instruction); // hoặc có thể để instruction riêng từng thuốc
                        prescriptionDetailRepository.save(detail);
                    }
                }
            }

            // 4. Cập nhật trạng thái lịch hẹn thành completed
            appointmentService.completeAppointment(appointmentId);

            redirectAttributes.addFlashAttribute("success", "Đã lưu bệnh án và kê đơn thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/doctor/dashboard";
    }

    // ========== XEM CHI TIẾT BỆNH ÁN CỦA MỘT LẦN KHÁM ==========
    @GetMapping("/medical-record/{appointmentId}")
    public String viewMedicalRecord(@PathVariable Long appointmentId, Model model, HttpSession session) {
        if (!isDoctor(session)) return "redirect:/login";

        Appointment appointment = appointmentService.findById(appointmentId);
        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointment(appointment).orElse(null);
        Prescription prescription = medicalRecord != null ? prescriptionRepository.findByMedicalRecord(medicalRecord).orElse(null) : null;
        List<PrescriptionDetail> details = prescription != null ? prescriptionDetailRepository.findByPrescription(prescription) : new ArrayList<>();

        model.addAttribute("appointment", appointment);
        model.addAttribute("medicalRecord", medicalRecord);
        model.addAttribute("prescription", prescription);
        model.addAttribute("prescriptionDetails", details);
        return "doctor/medical-record";
    }

    // ========== DANH SÁCH BỆNH NHÂN ĐÃ KHÁM CỦA BÁC SĨ (có phân trang, tìm kiếm) ==========
    @GetMapping("/medical-history")
    public String medicalHistoryList(Model model, HttpSession session,
                                     @RequestParam(defaultValue = "") String keyword,
                                     @RequestParam(defaultValue = "0") int page) {
        if (!isDoctor(session)) return "redirect:/login";

        Doctor doctor = getCurrentDoctor(session);
        if (doctor == null) return "redirect:/login";

        Pageable pageable = PageRequest.of(page, 10);
        // Query lấy danh sách bệnh nhân đã khám với bác sĩ này (chỉ lấy completed)
        Page<Object[]> results = appointmentRepository.findDistinctPatientsByDoctor(doctor.getId(), keyword, pageable);

        List<Map<String, Object>> patients = new ArrayList<>();
        for (Object[] row : results.getContent()) {
            User patient = (User) row[0];
            LocalDate lastExam = (LocalDate) row[1];
            Long totalExams = (Long) row[2];
            Map<String, Object> map = new HashMap<>();
            map.put("id", patient.getId());
            map.put("patientCode", patient.getPatientCode());
            map.put("fullName", patient.getFullName());
            map.put("gender", patient.getGender());
            map.put("dateOfBirth", patient.getDateOfBirth());
            map.put("lastExamDate", lastExam != null ? lastExam.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            map.put("totalExams", totalExams);
            patients.add(map);
        }

        model.addAttribute("patients", patients);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", results.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "doctor/medical-history";
    }

    @GetMapping("/medical-history/{patientId}")
    public String medicalHistory(@PathVariable Long patientId, Model model, HttpSession session) {
        if (!isDoctor(session)) return "redirect:/login";

        User patient = userRepository.findById(patientId).orElse(null);
        if (patient == null) return "redirect:/doctor/medical-history";

        // Lấy danh sách medical records
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findByAppointmentPatientOrderByAppointmentAppointmentDateDesc(patient);

        // Lấy prescription cho từng medical record
        Map<Long, Prescription> prescriptionMap = new HashMap<>();
        for (MedicalRecord mr : medicalRecords) {
            prescriptionRepository.findByMedicalRecord(mr).ifPresent(p -> prescriptionMap.put(mr.getId(), p));
        }

        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecords", medicalRecords);
        model.addAttribute("prescriptionMap", prescriptionMap);

        return "doctor/medical-record";
    }
}