package com.hospital.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DoctorDTO {

    private Long id;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotNull(message = "Vui lòng chọn chuyên khoa")
    private Long specialtyId;

    private String specialtyName;

    @NotNull(message = "Vui lòng chọn chức danh")
    private Long consultationTypeId;

    private String consultationTypeName;

    private String doctorCode;

    @NotBlank(message = "Học vị / Bằng cấp không được để trống")
    private String qualification;

    @Min(value = 0, message = "Số năm kinh nghiệm phải lớn hơn hoặc bằng 0")
    private Integer yearsOfExperience;

    private String bio;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String status;
}