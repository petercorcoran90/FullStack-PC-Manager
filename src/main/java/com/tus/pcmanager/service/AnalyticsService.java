package com.tus.pcmanager.service;

import com.tus.pcmanager.dto.CategoryStockDTO;
import com.tus.pcmanager.repository.HardwarePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final HardwarePartRepository partRepository;

    public List<CategoryStockDTO> getHardwareStockAnalytics() {
        return partRepository.getStockPerCategory();
    }
}