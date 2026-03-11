package com.tus.pcmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Name cannot be empty.")
    private String name;

    @NotBlank(message = "Manufacturer cannot be empty.")
    private String manufacturer;

    @NotBlank(message = "Category cannot be empty.")
    private String category;

    @NotNull(message = "Price is required.")
    @Min(value = 1, message = "Price must be greater than zero.")
    private BigDecimal price;

    @NotNull(message = "Stock level is required.")
    @Min(value = 0, message = "Stock level cannot be negative.")
    private Integer stockLevel;
}