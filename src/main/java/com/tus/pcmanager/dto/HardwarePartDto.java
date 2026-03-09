package com.tus.pcmanager.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HardwarePartDto {
    private Long id;
    private String name;
    private String manufacturer;
    private String category;
    private BigDecimal price;
    private Integer stockLevel;
}