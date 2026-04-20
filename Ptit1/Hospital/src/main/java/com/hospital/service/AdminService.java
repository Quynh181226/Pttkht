package com.hospital.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.hospital.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {
    // Dashboard
    long getTotalPatients();
    long getTotalDoctors();
    long getTotalAppointments();
    long getPendingPrescriptions();

    // Users
    Page<User> getUsers(String keyword, Pageable pageable);
    User getUserById(Long id);
    List<Appointment> getUserHistory(Long userId);

    // Specialties
    Page<Specialty> getSpecialties(String keyword, Pageable pageable);
    void saveSpecialty(Specialty specialty);
    Specialty getSpecialtyById(Long id);
    void toggleSpecialtyStatus(Long id, String status);
    void deleteSpecialty(Long id);

    // Doctors
    Page<Doctor> getDoctors(String keyword, Pageable pageable);
    void saveDoctor(Doctor doctor);
    Doctor getDoctorById(Long id);
    void toggleDoctorStatus(Long id, String status);
    List<Specialty> getAllSpecialties();
    List<ConsultationType> getAllConsultationTypes();

    // Medicines
    Page<Medicine> getMedicines(String keyword, Pageable pageable);
    void saveMedicine(Medicine medicine);
    Medicine getMedicineById(Long id);
    void toggleMedicineStatus(Long id, String status);
    boolean isMedicineCodeExists(String code);
    boolean isMedicineCodeExists(String code, Long excludeId);
    boolean isMedicineInUse(Long id);
    void deleteMedicine(Long id);

    // Prescriptions
    Page<Prescription> getPrescriptions(String keyword, Pageable pageable);
    void dispensePrescription(Long id);

    // Appointments
    Page<Appointment> getAppointments(String keyword, Pageable pageable);
    Appointment getAppointmentById(Long id);

    // Statistics
    Map<String, Object> getStatistics(String criteria, LocalDate fromDate, LocalDate toDate);
}