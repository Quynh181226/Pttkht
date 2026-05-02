package com.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @Column(name = "appointment_code", unique = true, nullable = false, length = 20)
    private String appointmentCode;

    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;

    @Column(name = "patient_phone", nullable = false, length = 15)
    private String patientPhone;

    @Column(name = "patient_email", length = 100)
    private String patientEmail;

    @Column(name = "patient_dob")
    private LocalDate patientDob;

    @Column(name = "patient_gender", length = 10)
    private String patientGender;

    @Column(name = "patient_address", columnDefinition = "TEXT")
    private String patientAddress;

    @Column(name = "identity_no", length = 20)
    private String identityNo;

    @Column(name = "insurance_no", length = 20)
    private String insuranceNo;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false, length = 20)
    private String slot; // MORNING, AFTERNOON

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}