package com.hospital.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionItemDTO {

    private Long id;

    @NotNull(message = "Thuốc không được để trống")
    private Long medicineId;

    private String medicineName;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private String dosage;

    private Integer duration;

    private String instruction;
}