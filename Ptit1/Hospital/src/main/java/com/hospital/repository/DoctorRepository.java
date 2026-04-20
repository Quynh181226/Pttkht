package com.hospital.repository;

import com.hospital.entity.Doctor;
import com.hospital.entity.Specialty;
import com.hospital.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
    List<Doctor> findBySpecialty(Specialty specialty);
    List<Doctor> findByStatus(Doctor.Status status);
    List<Doctor> findBySpecialtyAndStatus(Specialty specialty, Doctor.Status status);
    Page<Doctor> findByUserFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}