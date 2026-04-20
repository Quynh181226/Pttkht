package com.hospital.service.impl;

import com.hospital.entity.*;
import com.hospital.repository.*;
import com.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Transactional
    @Override
    public Appointment createAppointment(Long patientId, Long doctorId, Long shiftId, LocalDate appointmentDate, LocalTime appointmentTime, String reason, String patientNote) {
        if (appointmentRepository.findExistingAppointment(doctorId, appointmentDate, appointmentTime).isPresent()) {
            throw new RuntimeException("Khung giờ này đã có người đặt. Vui lòng chọn khung giờ khác.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca khám"));

        Appointment appointment = new Appointment();
        appointment.setBookingCode(generateBookingCode());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setShift(shift);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setReason(reason);
        appointment.setPatientNote(patientNote);
        appointment.setStatus(Appointment.Status.pending);
        appointment.setTotalFee(doctor.getConsultationType().getPrice());
        appointment.setCreatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    private String generateBookingCode() {
        return "BK" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient == null) return List.of();
        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = findById(id);
        if (appointment != null && appointment.getStatus() == Appointment.Status.pending) {
            appointment.setStatus(Appointment.Status.cancelled);
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    @Transactional
    @Override
    public Appointment completeAppointment(Long id) {
        Appointment appointment = findById(id);
        if (appointment != null && appointment.getStatus() == Appointment.Status.pending) {
            appointment.setStatus(Appointment.Status.completed);
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    @Override
    public MedicalRecord getMedicalRecordByAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            return medicalRecordRepository.findByAppointment(appointment).orElse(null);
        }
        return null;
    }
}