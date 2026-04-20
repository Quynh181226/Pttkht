package com.hospital.dto;

import com.hospital.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    private String password;
    private String confirmPassword;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private Gender gender;
    private LocalDate dateOfBirth;

    @NotBlank(message = "Số CCCD/CMND không được để trống")
    private String identityNumberHash;

    private String province;
    private String district;
    private String ward;
    private String streetAddress;
    private String insuranceNumber;
    private String patientCode;
    private String avatarUrl;
    private String role;
}