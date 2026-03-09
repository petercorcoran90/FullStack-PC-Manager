package com.tus.pcmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardwarePartDTO {
    private Long id;
    private String name;
    private String manufacturer;
    private String category;
    private BigDecimal price;
    private Integer stockLevel;
}