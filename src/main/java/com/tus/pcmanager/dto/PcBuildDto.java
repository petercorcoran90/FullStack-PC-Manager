package com.tus.pcmanager.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PcBuildDto {
    private Long id;
    private String buildName;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private List<HardwarePartDTO> parts;
}