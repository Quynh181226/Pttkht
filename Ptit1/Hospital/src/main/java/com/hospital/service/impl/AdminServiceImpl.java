package com.hospital.service.impl;

import com.hospital.entity.*;
import com.hospital.enums.Role;
import com.hospital.repository.*;
import com.hospital.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final MedicineRepository medicineRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final ConsultationTypeRepository consultationTypeRepository;

    public AdminServiceImpl(UserRepository userRepository, DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository, MedicineRepository medicineRepository, AppointmentRepository appointmentRepository, PrescriptionRepository prescriptionRepository, PrescriptionDetailRepository prescriptionDetailRepository, ConsultationTypeRepository consultationTypeRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
        this.medicineRepository = medicineRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionDetailRepository = prescriptionDetailRepository;
        this.consultationTypeRepository = consultationTypeRepository;
    }

    // ==================== DASHBOARD ====================
    @Override
    public long getTotalPatients() {
        return userRepository.countByRole(Role.patient);
    }

    @Override
    public long getTotalDoctors() {
        return doctorRepository.count();
    }

    @Override
    public long getTotalAppointments() {
        return appointmentRepository.count();
    }

    @Override
    public long getPendingPrescriptions() {
        return prescriptionRepository.countByStatus(Prescription.Status.pending);
    }

    // ==================== USERS ====================
    @Override
    public Page<User> getUsers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                    keyword, keyword, keyword, pageable);
        }
        return userRepository.findByRole(Role.patient, pageable);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<Appointment> getUserHistory(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            return appointmentRepository.findByPatient(user);
        }
        return new ArrayList<>();
    }

    // ==================== SPECIALTIES ====================
    @Override
    public Page<Specialty> getSpecialties(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return specialtyRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
        return specialtyRepository.findAll(pageable);
    }

    @Override
    public void saveSpecialty(Specialty specialty) {
        specialtyRepository.save(specialty);
    }

    @Override
    public Specialty getSpecialtyById(Long id) {
        return specialtyRepository.findById(id).orElse(null);
    }

    @Override
    public void toggleSpecialtyStatus(Long id, String status) {
        Specialty specialty = getSpecialtyById(id);
        if (specialty != null) {
            specialty.setStatus(Specialty.Status.valueOf(status));
            specialtyRepository.save(specialty);
        }
    }

    @Override
    public void deleteSpecialty(Long id) {
        Specialty specialty = getSpecialtyById(id);
        if (specialty != null) {
            specialtyRepository.delete(specialty);
        }
    }

    // ==================== DOCTORS ====================
    @Override
    public Page<Doctor> getDoctors(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return doctorRepository.findByUserFullNameContainingIgnoreCase(keyword, pageable);
        }
        return doctorRepository.findAll(pageable);
    }

    @Override
    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    @Override
    public void toggleDoctorStatus(Long id, String status) {
        Doctor doctor = getDoctorById(id);
        if (doctor != null) {
            doctor.setStatus(Doctor.Status.valueOf(status));
            doctorRepository.save(doctor);
        }
    }

    @Override
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    @Override
    public List<ConsultationType> getAllConsultationTypes() {
        return consultationTypeRepository.findAll();
    }

    // ==================== MEDICINES ====================
    @Override
    public Page<Medicine> getMedicines(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return medicineRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword, pageable);
        }
        return medicineRepository.findAll(pageable);
    }

    @Override
    public void saveMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
    }

    @Override
    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id).orElse(null);
    }

    @Override
    public void toggleMedicineStatus(Long id, String status) {
        Medicine medicine = getMedicineById(id);
        if (medicine != null) {
            medicine.setStatus(Medicine.Status.valueOf(status));
            medicineRepository.save(medicine);
        }
    }

    @Override
    public boolean isMedicineCodeExists(String code) {
        return medicineRepository.findByCode(code).isPresent();
    }

    @Override
    public boolean isMedicineCodeExists(String code, Long excludeId) {
        return medicineRepository.findByCodeAndIdNot(code, excludeId).isPresent();
    }

    @Override
    public boolean isMedicineInUse(Long id) {
        return prescriptionDetailRepository.countByMedicineId(id) > 0;
    }

    @Override
    public void deleteMedicine(Long id) {
        if (isMedicineInUse(id)) {
            throw new RuntimeException("Không thể xóa thuốc này vì đã có trong đơn thuốc! Vui lòng ngừng bán thay vì xóa.");
        }
        medicineRepository.deleteById(id);
    }

    // ==================== PRESCRIPTIONS ====================
    @Override
    public Page<Prescription> getPrescriptions(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return prescriptionRepository.findByMedicalRecordAppointmentPatientFullNameContainingIgnoreCase(keyword, pageable);
        }
        return prescriptionRepository.findByStatus(Prescription.Status.pending, pageable);
    }

    @Override
    public void dispensePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id).orElse(null);
        if (prescription != null) {
            prescription.setStatus(Prescription.Status.dispensed);
            prescription.setUpdatedAt(java.time.LocalDateTime.now());
            prescriptionRepository.save(prescription);
        }
    }

    // ==================== APPOINTMENTS ====================
    @Override
    public Page<Appointment> getAppointments(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return appointmentRepository.findByPatientFullNameContainingIgnoreCaseOrDoctorUserFullNameContainingIgnoreCase(keyword, pageable);
        }
        return appointmentRepository.findAll(pageable);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    // ==================== STATISTICS ====================
    @Override
    public Map<String, Object> getStatistics(String criteria, LocalDate fromDate, LocalDate toDate) {
        Map<String, Object> result = new HashMap<>();

        if (fromDate == null) fromDate = LocalDate.now().minusMonths(1);
        if (toDate == null) toDate = LocalDate.now();

        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(fromDate, toDate);
        appointments = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.completed)
                .collect(Collectors.toList());

        // Total revenue
        BigDecimal totalRevenue = appointments.stream()
                .map(Appointment::getTotalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total appointments
        long totalAppointments = appointments.size();

        // Average per day
        double averageDaily = appointments.stream()
                .collect(Collectors.groupingBy(a -> a.getAppointmentDate().toString(), Collectors.counting()))
                .values().stream().mapToLong(Long::longValue).average().orElse(0);

        // Group by criteria
        Map<String, List<Appointment>> grouped = new LinkedHashMap<>();
        DateTimeFormatter formatter;

        switch (criteria) {
            case "day":
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                appointments.forEach(a -> {
                    String key = a.getAppointmentDate().format(formatter);
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
                });
                break;
            case "month":
                formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                appointments.forEach(a -> {
                    String key = a.getAppointmentDate().format(formatter);
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
                });
                break;
            case "year":
                formatter = DateTimeFormatter.ofPattern("yyyy");
                appointments.forEach(a -> {
                    String key = a.getAppointmentDate().format(formatter);
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
                });
                break;
            case "specialty":
                appointments.forEach(a -> {
                    String key = a.getDoctor().getSpecialty().getName();
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
                });
                break;
            case "doctor":
                appointments.forEach(a -> {
                    String key = a.getDoctor().getUser().getFullName();
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
                });
                break;
            default:
                appointments.forEach(a -> {
                    String key = a.getAppointmentDate().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
                });
        }

        List<Map<String, Object>> statistics = new ArrayList<>();
        for (Map.Entry<String, List<Appointment>> entry : grouped.entrySet()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("period", entry.getKey());
            stat.put("count", entry.getValue().size());
            BigDecimal revenue = entry.getValue().stream()
                    .map(Appointment::getTotalFee)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stat.put("revenue", revenue);
            statistics.add(stat);
        }

        result.put("statistics", statistics);
        result.put("totalRevenue", totalRevenue);
        result.put("totalAppointments", totalAppointments);
        result.put("averageDaily", Math.round(averageDaily));

        return result;
    }
}