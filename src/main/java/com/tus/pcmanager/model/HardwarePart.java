package com.tus.pcmanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "hardware_parts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HardwarePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String manufacturer;
    private String category;
    private BigDecimal price;
    private Integer stockLevel;
}