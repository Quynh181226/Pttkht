package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "doctors")
@Data
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @ManyToOne
    @JoinColumn(name = "consultation_type_id", nullable = false)
    private ConsultationType consultationType;

    @Column(name = "doctor_code", unique = true, nullable = false)
    private String doctorCode;

    @Column(nullable = false)
    private String qualification;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience = 0;

    private String bio;

    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    public enum Status {
        active, inactive, on_leave
    }
}