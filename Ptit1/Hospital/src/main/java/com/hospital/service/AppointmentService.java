package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.entity.MedicalRecord;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Long patientId, Long doctorId, Long shiftId, LocalDate appointmentDate, LocalTime appointmentTime, String reason, String patientNote);

    List<Appointment> getAppointmentsByPatient(Long patientId);

    Appointment findById(Long id);

    Appointment cancelAppointment(Long id);

    Appointment completeAppointment(Long id);

    MedicalRecord getMedicalRecordByAppointment(Long appointmentId);
}