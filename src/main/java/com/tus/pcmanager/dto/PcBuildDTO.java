package com.tus.pcmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcBuildDTO {
    private Long id;
    private String buildName;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private List<HardwarePartDTO> parts;
}