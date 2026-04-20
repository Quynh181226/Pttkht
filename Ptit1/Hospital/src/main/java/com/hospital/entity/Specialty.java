package com.hospital.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "specialties")
@Data
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

//    @OneToMany(mappedBy = "specialty")
//    @JsonIgnore
//    private List<Doctor> doctors;

    @Enumerated(EnumType.STRING)
    private Status status = Status.active;

    public enum Status {
        active, inactive
    }
}