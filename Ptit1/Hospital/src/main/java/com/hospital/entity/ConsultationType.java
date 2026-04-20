package com.hospital.entity;// ConsultationType.java
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "consultation_types")
@Data
public class ConsultationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    public enum Status {
        active, inactive
    }
}