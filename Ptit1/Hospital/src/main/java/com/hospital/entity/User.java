package com.hospital.entity;
import com.hospital.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String gender;

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "identity_number_hash")
    private String identityNumberHash;

    private String ethnicity;
    private String province;
    private String district;
    private String ward;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "insurance_number")
    private String insuranceNumber;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "patient_code", unique = true)
    private String patientCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.patient;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}