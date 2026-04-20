package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.appointmentTime = :time")
    Optional<Appointment> findExistingAppointment(@Param("doctorId") Long doctorId,
                                                  @Param("date") LocalDate date,
                                                  @Param("time") LocalTime time);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status = 'pending'")
    List<Appointment> findPendingAppointmentsByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    // ========== ADMIN ==========
    Page<Appointment> findAll(Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.doctor.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Appointment> findByPatientFullNameContainingIgnoreCaseOrDoctorUserFullNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

    List<Appointment> findByAppointmentDateBetween(LocalDate fromDate, LocalDate toDate);

    // ========== DOCTOR ==========
    // Phân trang danh sách chờ khám
    Page<Appointment> findByDoctorAndAppointmentDateAndStatus(Doctor doctor, LocalDate date, Appointment.Status status, Pageable pageable);

    // Lấy danh sách bệnh nhân đã khám (unique) của bác sĩ
    @Query("SELECT a.patient, MAX(a.appointmentDate), COUNT(a) FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND a.status = 'completed' " +
            "AND (LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.patient.patientCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "GROUP BY a.patient ORDER BY MAX(a.appointmentDate) DESC")
    Page<Object[]> findDistinctPatientsByDoctor(@Param("doctorId") Long doctorId,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);

}