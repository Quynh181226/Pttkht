package com.hospital.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String fullname;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // USER, ADMIN

    private LocalDate dob;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "identity_no", length = 20)
    private String identityNo;

    @Column(name = "insurance_no", length = 20)
    private String insuranceNo;

    @OneToMany(mappedBy = "user")
    private List<Appointment> appointments = new ArrayList<>();
}