package com.hospital.repository;

import com.hospital.entity.MedicalRecord;
import com.hospital.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    long countByStatus(Prescription.Status status);
    Optional<Prescription> findByMedicalRecord(MedicalRecord medicalRecord);
    Page<Prescription> findByStatus(Prescription.Status status, Pageable pageable);
    Page<Prescription> findByMedicalRecordAppointmentPatientFullNameContainingIgnoreCase(String patientName, Pageable pageable);
}