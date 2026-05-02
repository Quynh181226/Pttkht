package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenueDTO {
    private String doctorName;
    private Long patientCount;
    private BigDecimal revenue;
}