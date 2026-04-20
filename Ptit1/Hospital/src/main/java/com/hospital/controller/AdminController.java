package com.hospital.controller;

import com.hospital.dto.DoctorDTO;
import com.hospital.dto.MedicineDTO;
import com.hospital.dto.UserDTO;
import com.hospital.entity.*;

import com.hospital.enums.Role;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PrescriptionDetailRepository;
import com.hospital.repository.UserRepository;
import com.hospital.service.AdminService;
import com.hospital.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository;

    @Autowired
    private AppointmentService appointmentService;

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "admin".equals(role);
    }

    private void addUserToModel(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = adminService.getUserById(userId);
            if (user != null) {
                model.addAttribute("currentUser", user);
            }
        }
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userEmail", session.getAttribute("userEmail"));
        model.addAttribute("avatarUrl", session.getAttribute("avatarUrl"));
    }

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        model.addAttribute("totalPatients", adminService.getTotalPatients());
        model.addAttribute("totalDoctors", adminService.getTotalDoctors());
        model.addAttribute("totalAppointments", adminService.getTotalAppointments());
        model.addAttribute("pendingPrescriptions", adminService.getPendingPrescriptions());

        return "admin/dashboard";
    }

    // ==================== USERS ====================
    @GetMapping("/users")
    public String users(Model model, HttpSession session,
                        @RequestParam(defaultValue = "") String keyword,
                        @RequestParam(defaultValue = "0") int page) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Page<User> userPage = adminService.getUsers(keyword, PageRequest.of(page, 10, Sort.by("id").descending()));

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        if (!model.containsAttribute("userDTO")) {
            model.addAttribute("userDTO", new UserDTO());
        }

        return "admin/users";
    }

    @GetMapping("/users/{id}/history")
    public String userHistory(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        User patient = adminService.getUserById(id);
        List<Appointment> appointments = adminService.getUserHistory(id);

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);

        return "admin/user-history";
    }

    // ==================== SPECIALTIES ====================
    @GetMapping("/specialties")
    public String specialties(Model model, HttpSession session,
                              @RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "0") int page) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Page<Specialty> specialtyPage = adminService.getSpecialties(keyword, PageRequest.of(page, 10));

        model.addAttribute("specialties", specialtyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", specialtyPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "admin/specialties";
    }

    @PostMapping("/specialties/save")
    public String saveSpecialty(@ModelAttribute Specialty specialty, RedirectAttributes redirectAttributes) {
        adminService.saveSpecialty(specialty);
        redirectAttributes.addFlashAttribute("success", "Lưu chuyên khoa thành công");
        return "redirect:/admin/specialties";
    }

    @GetMapping("/specialties/create")
    public String createSpecialtyForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);
        // Chỉ cần redirect về specialties và mở modal
        return "redirect:/admin/specialties?openModal=true";
    }

    @GetMapping("/specialties/edit/{id}")
    public String editSpecialtyForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);
        // Redirect về specialties và truyền id để mở modal edit
        return "redirect:/admin/specialties?editId=" + id;
    }

    @PostMapping("/specialties/{id}/status")
    public String toggleSpecialtyStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        try {
            // Nếu đang cố gắng ẨN chuyên khoa (INACTIVE)
            if ("inactive".equals(status)) {
                // Kiểm tra xem có bác sĩ nào thuộc chuyên khoa này đang hoạt động không
                Specialty specialty = adminService.getSpecialtyById(id);
                if (specialty != null) {
                    List<Doctor> activeDoctors = doctorRepository.findBySpecialtyAndStatus(specialty, Doctor.Status.active);
                    if (!activeDoctors.isEmpty()) {
                        redirectAttributes.addFlashAttribute("error",
                                "Không thể ẩn chuyên khoa này vì vẫn còn " + activeDoctors.size() + " bác sĩ đang hoạt động thuộc chuyên khoa này!");
                        return "redirect:/admin/specialties";
                    }
                }
            }

            adminService.toggleSpecialtyStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/specialties";
    }

    @PostMapping("/specialties/{id}/delete")
    public String deleteSpecialty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Specialty specialty = adminService.getSpecialtyById(id);
            if (specialty != null) {
                // Kiểm tra có bác sĩ nào thuộc chuyên khoa này không
                List<Doctor> doctors = doctorRepository.findBySpecialty(specialty);
                if (!doctors.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error",
                            "Không thể xóa chuyên khoa này vì đã có " + doctors.size() + " bác sĩ thuộc chuyên khoa! Vui lòng ẩn thay vì xóa.");
                    return "redirect:/admin/specialties";
                }
                adminService.deleteSpecialty(id);
                redirectAttributes.addFlashAttribute("success", "Xóa chuyên khoa thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/specialties";
    }

    @GetMapping("/specialties/api/{id}")
    @ResponseBody
    public Map<String, Object> getSpecialtyApi(@PathVariable Long id) {
        Specialty specialty = adminService.getSpecialtyById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("id", specialty.getId());
        result.put("name", specialty.getName());
        result.put("description", specialty.getDescription());
        result.put("status", specialty.getStatus().name());
        return result;
    }

    // ==================== DOCTORS ====================
    @GetMapping("/medicines")
    public String medicines(Model model, HttpSession session,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "0") int page) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Pageable pageable = PageRequest.of(page, 10);
        Page<Medicine> medicinePage = adminService.getMedicines(keyword, pageable);

        // Lấy danh sách ID thuốc đã có trong prescription_detail (không thể xóa)
        List<Long> medicineIdsInUse = prescriptionDetailRepository.findDistinctMedicineIds();

        model.addAttribute("medicines", medicinePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", medicinePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("medicineIdsInUse", medicineIdsInUse);  // THÊM DÒNG NÀY

        return "admin/medicines";
    }

    @PostMapping("/medicines/save")
    public String saveMedicine(@Valid @ModelAttribute("medicineDTO") MedicineDTO medicineDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";

        // Kiểm tra trùng mã thuốc
        if (medicineDTO.getId() == null) {
            if (adminService.isMedicineCodeExists(medicineDTO.getCode())) {
                bindingResult.rejectValue("code", "error.medicineDTO", "Mã thuốc đã tồn tại!");
            }
        } else {
            if (adminService.isMedicineCodeExists(medicineDTO.getCode(), medicineDTO.getId())) {
                bindingResult.rejectValue("code", "error.medicineDTO", "Mã thuốc đã tồn tại!");
            }
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("medicineDTO", medicineDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.medicineDTO", bindingResult);
            return "redirect:/admin/medicines/create";
        }

        try {
            Medicine medicine;
            if (medicineDTO.getId() != null) {
                medicine = adminService.getMedicineById(medicineDTO.getId());
                if (medicine == null) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy thuốc!");
                    return "redirect:/admin/medicines";
                }
            } else {
                medicine = new Medicine();
            }

            // Set các field từ DTO
            medicine.setCode(medicineDTO.getCode().toUpperCase()); // chuyển sang chữ hoa
            medicine.setName(medicineDTO.getName());
            medicine.setUnit(medicineDTO.getUnit());
            medicine.setStrength(medicineDTO.getStrength());
            medicine.setStockQuantity(medicineDTO.getStockQuantity() != null ? medicineDTO.getStockQuantity() : 0);
            medicine.setUnitPrice(medicineDTO.getUnitPrice());
            medicine.setExpiryDate(medicineDTO.getExpiryDate());
            medicine.setManufacturer(medicineDTO.getManufacturer());
            medicine.setRequiresPrescription(medicineDTO.getRequiresPrescription());
            if (medicineDTO.getStatus() != null) {
                medicine.setStatus(Medicine.Status.valueOf(medicineDTO.getStatus()));
            }

            adminService.saveMedicine(medicine);
            redirectAttributes.addFlashAttribute("success", "Lưu thuốc thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/medicines";
    }

    @PostMapping("/medicines/{id}/status")
    public String toggleMedicineStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleMedicineStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/medicines";
    }

    @PostMapping("/medicines/{id}/delete")
    public String deleteMedicine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (adminService.isMedicineInUse(id)) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa thuốc này vì đã có trong đơn thuốc! Vui lòng ngừng bán thay vì xóa.");
            } else {
                adminService.deleteMedicine(id);
                redirectAttributes.addFlashAttribute("success", "Xóa thuốc thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/medicines";
    }

    // ==================== PRESCRIPTIONS ====================
    @GetMapping("/prescriptions")
    public String prescriptions(Model model, HttpSession session,
                                @RequestParam(defaultValue = "") String keyword,
                                @RequestParam(defaultValue = "0") int page) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Page<Prescription> prescriptionPage = adminService.getPrescriptions(keyword, PageRequest.of(page, 10, Sort.by("createdAt").descending()));

        model.addAttribute("prescriptions", prescriptionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", prescriptionPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "admin/prescriptions";
    }

    @PostMapping("/prescriptions/{id}/dispense")
    public String dispensePrescription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.dispensePrescription(id);
        redirectAttributes.addFlashAttribute("success", "Đã cấp phát đơn thuốc");
        return "redirect:/admin/prescriptions";
    }

    // ==================== APPOINTMENTS ====================
    @GetMapping("/appointments")
    public String appointments(Model model, HttpSession session,
                               @RequestParam(defaultValue = "") String keyword,
                               @RequestParam(defaultValue = "0") int page) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Page<Appointment> appointmentPage = adminService.getAppointments(keyword, PageRequest.of(page, 5, Sort.by("appointmentDate").descending()));

        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "admin/appointments";
    }

    @GetMapping("/appointments/{id}")
    public String appointmentDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Appointment appointment = adminService.getAppointmentById(id);
        MedicalRecord medicalRecord = appointmentService.getMedicalRecordByAppointment(id);

        model.addAttribute("appointment", appointment);
        model.addAttribute("medicalRecord", medicalRecord);

        return "admin/appointment-detail";
    }

    // ==================== STATISTICS ====================
    @GetMapping("/statistics")
    public String statistics(Model model, HttpSession session,
                             @RequestParam(defaultValue = "month") String criteria,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Map<String, Object> stats = adminService.getStatistics(criteria, fromDate, toDate);

        model.addAttribute("statistics", stats.get("statistics"));
        model.addAttribute("totalRevenue", stats.get("totalRevenue"));
        model.addAttribute("totalAppointments", stats.get("totalAppointments"));
        model.addAttribute("averageDaily", stats.get("averageDaily"));
        model.addAttribute("criteria", criteria);
        model.addAttribute("fromDate", fromDate != null ? fromDate : LocalDate.now().minusMonths(1));
        model.addAttribute("toDate", toDate != null ? toDate : LocalDate.now());

        return "admin/statistics";
    }


    @GetMapping("/users/create")
    public String createUserForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);
        model.addAttribute("user", adminService.getUserById(id));
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login-mana";

        if (userDTO.getUsername() != null && !userDTO.getUsername().trim().isEmpty()) {
            if (userDTO.getUsername().length() < 3 || userDTO.getUsername().length() > 50) {
                bindingResult.rejectValue("username", "error.userDTO", "Tên đăng nhập phải từ 3 đến 50 ký tự");
            }
        }

        if (userDTO.getFullName() != null && !userDTO.getFullName().trim().isEmpty()) {
            if (userDTO.getFullName().length() < 2 || userDTO.getFullName().length() > 100) {
                bindingResult.rejectValue("fullName", "error.userDTO", "Họ tên phải từ 2 đến 100 ký tự");
            }
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().trim().isEmpty()) {
            if (!userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                bindingResult.rejectValue("email", "error.userDTO", "Email không hợp lệ (VD: ten@example.com)");
            }
        }

        if (userDTO.getPhone() != null && !userDTO.getPhone().trim().isEmpty()) {
            if (!userDTO.getPhone().matches("^(0|\\+84)(3|5|7|8|9)[0-9]{8}$")) {
                bindingResult.rejectValue("phone", "error.userDTO", "Số điện thoại không hợp lệ (VD: 0912345678)");
            }
        }

        if (userDTO.getIdentityNumberHash() != null && !userDTO.getIdentityNumberHash().trim().isEmpty()) {
            if (!userDTO.getIdentityNumberHash().matches("^[0-9]{9,12}$")) {
                bindingResult.rejectValue("identityNumberHash", "error.userDTO", "Số CCCD/CMND phải từ 9 đến 12 chữ số");
            }
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userDTO", userDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDTO", bindingResult);
            redirectAttributes.addFlashAttribute("openModal", true);
            return "redirect:/admin/users";
        }

        try {
            User user;
            if (userDTO.getId() != null && userDTO.getId() > 0) {
                // === UPDATE ===
                user = adminService.getUserById(userDTO.getId());
                if (user == null) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy bệnh nhân!");
                    return "redirect:/admin/users";
                }

                user.setFullName(userDTO.getFullName());
                user.setEmail(userDTO.getEmail());
                user.setPhone(userDTO.getPhone());
                user.setUsername(userDTO.getUsername());
                user.setDateOfBirth(userDTO.getDateOfBirth());
                user.setIdentityNumberHash(userDTO.getIdentityNumberHash());
                user.setProvince(userDTO.getProvince());
                user.setDistrict(userDTO.getDistrict());
                user.setWard(userDTO.getWard());
                user.setStreetAddress(userDTO.getStreetAddress());
                user.setInsuranceNumber(userDTO.getInsuranceNumber());
                user.setAvatarUrl(userDTO.getAvatarUrl());

    //            if (userDTO.getGender() != null) {
    //                user.setGender(String.valueOf(userDTO.getGender()));
    //            }
                if (userDTO.getGender() != null) {
                    user.setGender(userDTO.getGender().name().toLowerCase());
                } else {
                    user.setGender("other");
                }

                userRepository.save(user);
                redirectAttributes.addFlashAttribute("success", "Cập nhật bệnh nhân thành công!");

            } else {
                // === CREATE NEW ===
                if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Mật khẩu không được để trống khi tạo mới!");
                    return "redirect:/admin/users/create";
                }

                user = new User();
                user.setUsername(userDTO.getUsername());
                user.setPassword(userDTO.getPassword());
                user.setFullName(userDTO.getFullName());
                user.setEmail(userDTO.getEmail());
                user.setPhone(userDTO.getPhone());
                user.setRole(Role.patient);
                user.setCreatedAt(LocalDateTime.now());
                user.setDateOfBirth(userDTO.getDateOfBirth());
                user.setIdentityNumberHash(userDTO.getIdentityNumberHash());
                user.setProvince(userDTO.getProvince());
                user.setDistrict(userDTO.getDistrict());
                user.setWard(userDTO.getWard());
                user.setStreetAddress(userDTO.getStreetAddress());
                user.setInsuranceNumber(userDTO.getInsuranceNumber());
                user.setAvatarUrl(userDTO.getAvatarUrl());
                user.setPatientCode("PAT-" + System.currentTimeMillis());

                if (userDTO.getGender() != null) {
                    user.setGender(userDTO.getGender().name().toLowerCase());
                } else {
                    user.setGender("other");
                }

                userRepository.save(user);
                redirectAttributes.addFlashAttribute("success", "Thêm bệnh nhân thành công!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/users";
    }

    // ==================== DOCTORS ====================
    @GetMapping("/doctors")
    public String doctors(Model model, HttpSession session,
                          @RequestParam(defaultValue = "") String keyword,
                          @RequestParam(defaultValue = "0") int page) {

        if (!isAdmin(session)) return "redirect:/login-mana";
        addUserToModel(model, session);

        Pageable pageable = PageRequest.of(page, 10);
        Page<Doctor> doctorPage = adminService.getDoctors(keyword, pageable);

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("specialties", adminService.getAllSpecialties());
        model.addAttribute("consultationTypes", adminService.getAllConsultationTypes());

        if (!model.containsAttribute("doctorDTO")) {
            model.addAttribute("doctorDTO", new DoctorDTO());
        }

        return "admin/doctors";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@Valid @ModelAttribute("doctorDTO") DoctorDTO doctorDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login-mana";

        if (doctorDTO.getFullName() != null && !doctorDTO.getFullName().trim().isEmpty()) {
            if (doctorDTO.getFullName().length() < 2 || doctorDTO.getFullName().length() > 100) {
                bindingResult.rejectValue("fullName", "error.doctorDTO", "Họ tên phải từ 2 đến 100 ký tự");
            }
        }

        if(doctorDTO.getQualification() != null && !doctorDTO.getQualification().trim().isEmpty()) {
            if(doctorDTO.getQualification().length() < 6 || doctorDTO.getQualification().length() > 100) {
                bindingResult.rejectValue("qualification", "error.doctorDTO", "Học vị / Bằng cấp phải từ 6 đến 100 ký tự");
            }
        }

        if (doctorDTO.getEmail() != null && !doctorDTO.getEmail().trim().isEmpty()) {
            if (!doctorDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                bindingResult.rejectValue("email", "error.doctorDTO", "Email không hợp lệ");
            }
        }

        if (doctorDTO.getPhone() != null && !doctorDTO.getPhone().trim().isEmpty()) {
            if (!doctorDTO.getPhone().matches("^(0|\\+84)(3|5|7|8|9)[0-9]{8}$")) {
                bindingResult.rejectValue("phone", "error.doctorDTO", "Số điện thoại không hợp lệ");
            }
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("doctorDTO", doctorDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.doctorDTO", bindingResult);
            redirectAttributes.addFlashAttribute("openModal", true);
            return "redirect:/admin/doctors";
        }

        try {
            Doctor doctor;
            User user;

            if (doctorDTO.getId() != null && doctorDTO.getId() > 0) {
                doctor = adminService.getDoctorById(doctorDTO.getId());
                user = doctor.getUser();

                user.setFullName(doctorDTO.getFullName());
                user.setEmail(doctorDTO.getEmail());
                user.setPhone(doctorDTO.getPhone());
                userRepository.save(user);

                doctor.setSpecialty(adminService.getSpecialtyById(doctorDTO.getSpecialtyId()));
                doctor.setConsultationType(adminService.getAllConsultationTypes().stream()
                        .filter(ct -> ct.getId().equals(doctorDTO.getConsultationTypeId()))
                        .findFirst().orElse(null));
                doctor.setYearsOfExperience(doctorDTO.getYearsOfExperience() != null ? doctorDTO.getYearsOfExperience() : 0);
                doctor.setQualification(doctorDTO.getQualification());
                doctor.setBio(doctorDTO.getBio());

                if (doctorDTO.getStatus() != null) {
                    doctor.setStatus(Doctor.Status.valueOf(doctorDTO.getStatus()));
                }

                adminService.saveDoctor(doctor);
                redirectAttributes.addFlashAttribute("success", "Cập nhật bác sĩ thành công!");

            } else {
                user = new User();
                user.setUsername(doctorDTO.getEmail().split("@")[0]);
                user.setPassword("123456");
                user.setEmail(doctorDTO.getEmail());
                user.setPhone(doctorDTO.getPhone());
                user.setFullName(doctorDTO.getFullName());
                user.setRole(Role.doctor);
//                user.setStatus(User.Status.active);
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);

                doctor = new Doctor();
                doctor.setUser(user);
                doctor.setDoctorCode("BS" + System.currentTimeMillis());
                doctor.setSpecialty(adminService.getSpecialtyById(doctorDTO.getSpecialtyId()));
                doctor.setConsultationType(adminService.getAllConsultationTypes().stream()
                        .filter(ct -> ct.getId().equals(doctorDTO.getConsultationTypeId()))
                        .findFirst().orElse(null));
                doctor.setYearsOfExperience(doctorDTO.getYearsOfExperience() != null ? doctorDTO.getYearsOfExperience() : 0);
                doctor.setQualification(doctorDTO.getQualification());
                doctor.setBio(doctorDTO.getBio());
                doctor.setStatus(Doctor.Status.active);

                adminService.saveDoctor(doctor);
                redirectAttributes.addFlashAttribute("success", "Thêm bác sĩ thành công!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/doctors";
    }

    @PostMapping("/doctors/{id}/status")
    public String toggleDoctorStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        adminService.toggleDoctorStatus(id, status.toLowerCase());
        redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công");
        return "redirect:/admin/doctors";
    }
}