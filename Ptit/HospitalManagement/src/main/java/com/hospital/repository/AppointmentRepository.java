package com.hospital.repository;

import com.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientPhoneOrderByCreatedAtDesc(String phone);
    List<Appointment> findByStatus(String status);
    List<Appointment> findByDoctorIdAndAppointmentDateAndSlot(Long doctorId, LocalDate date, String slot);
    Optional<Appointment> findByAppointmentCode(String code);
    List<Appointment> findByDoctorIdOrderByAppointmentDateAsc(Long doctorId);
    long countByDoctorIdAndAppointmentDateAndSlotAndStatusNot(Long doctorId, LocalDate date, String slot, String status);
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateDesc(Long doctorId, String status);

    long countByAppointmentDateBetween(LocalDate from, LocalDate to);

    // SỬA: Dùng patientPhone thay vì patientId
    @Query("SELECT COUNT(DISTINCT a.patientPhone) FROM Appointment a WHERE a.appointmentDate BETWEEN :from AND :to")
    long countDistinctPatientPhoneByAppointmentDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}