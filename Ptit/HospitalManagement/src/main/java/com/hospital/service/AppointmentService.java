// service/AppointmentService.java
package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    private String generateCode() {
        return "KH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public Appointment createAppointment(Appointment appointment) {
        appointment.setAppointmentCode(generateCode());
        appointment.setStatus("PENDING");
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByPhone(String phone) {
        return appointmentRepository.findByPatientPhoneOrderByCreatedAtDesc(phone);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment getAppointmentByCode(String code) {
        return appointmentRepository.findByAppointmentCode(code).orElse(null);
    }

    public boolean cancelAppointment(Long id) {
        Appointment a = getAppointmentById(id);
        if (a != null && !"CANCELLED".equals(a.getStatus())) {
            a.setStatus("CANCELLED");
            appointmentRepository.save(a);
            return true;
        }
        return false;
    }

    public boolean updateStatus(Long id, String status) {
        Appointment a = getAppointmentById(id);
        if (a != null) {
            a.setStatus(status);
            appointmentRepository.save(a);
            return true;
        }
        return false;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public boolean isSlotAvailable(Long doctorId, LocalDate date, String slot) {
        long count = appointmentRepository.countByDoctorIdAndAppointmentDateAndSlotAndStatusNot(
                doctorId, date, slot, "CANCELLED"
        );
        return count < 15;
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateAndSlot(doctorId, date, null);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateAsc(doctorId);
    }

    public List<Appointment> getCompletedAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndStatusOrderByAppointmentDateDesc(doctorId, "COMPLETED");
    }

    public long countByDateRange(LocalDate from, LocalDate to) {
        return appointmentRepository.countByAppointmentDateBetween(from, to);
    }

    public long countDistinctPatientsByDateRange(LocalDate from, LocalDate to) {
        return appointmentRepository.countDistinctPatientPhoneByAppointmentDateBetween(from, to);
    }
}