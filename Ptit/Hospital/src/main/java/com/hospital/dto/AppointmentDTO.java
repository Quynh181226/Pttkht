package com.hospital.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AppointmentDTO {
    @NotBlank(message = "Họ tên không được để trống")
    private String patientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{10,15}$", message = "Số điện thoại không hợp lệ")
    private String patientPhone;

    @Email(message = "Email không hợp lệ")
    private String patientEmail;

    private LocalDate patientDob;
    private String patientGender;
    private String patientAddress;
    private String identityNo;
    private String insuranceNo;

    @NotNull(message = "Vui lòng chọn bác sĩ")
    private Long doctorId;

    @NotNull(message = "Vui lòng chọn khoa")
    private Long departmentId;

    @NotNull(message = "Vui lòng chọn ngày khám")
    private LocalDate appointmentDate;

    @NotBlank(message = "Vui lòng chọn ca khám")
    private String slot;

    private String symptoms;
}