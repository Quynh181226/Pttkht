package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "medicines")
@Data
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    private String unit;           // Viên, Ống, Gói, Chai, Lọ, Tuýp, Hộp

    private String strength;       // Ex: 500mg

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    private String manufacturer;

    @Column(name = "requires_prescription")
    private Boolean requiresPrescription = true;

    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    public enum Status {
        active, inactive
    }
}