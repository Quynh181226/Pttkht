package com.hospital.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MedicineDTO {

    private Long id;

    @NotBlank(message = "Mã thuốc không được để trống")
    @Size(min = 3, max = 50, message = "Mã thuốc phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Mã thuốc chỉ gồm chữ in hoa, số và dấu gạch ngang (VD: PAR-001)")
    private String code;

    @NotBlank(message = "Tên thuốc không được để trống")
    @Size(min = 2, max = 200, message = "Tên thuốc phải từ 2 đến 200 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng chọn đơn vị tính")
    private String unit;

    private String strength;

    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity = 0;

    @NotNull(message = "Đơn giá không được để trống")
    @Min(value = 0, message = "Đơn giá phải lớn hơn hoặc bằng 0")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Future(message = "Hạn sử dụng phải là ngày trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Size(max = 200, message = "Nhà sản xuất không quá 200 ký tự")
    private String manufacturer;

    private Boolean requiresPrescription = true;

    @NotBlank(message = "Vui lòng chọn trạng thái")
    private String status;
}