package com.hospital.service;

import com.hospital.entity.MedicalRecord;
import com.hospital.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;

    public Optional<MedicalRecord> getByAppointmentId(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId);
    }

    public MedicalRecord saveMedicalRecord(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    public List<MedicalRecord> getMedicalRecordsByPatientPhone(String phone) {
        return medicalRecordRepository.findByAppointment_PatientPhoneOrderByCreatedAtDesc(phone);
    }
}