package com.tus.pcmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.dto.CategoryStockDTO;
import com.tus.pcmanager.repository.HardwarePartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

class AnalyticsServiceTest {

    private AnalyticsService analyticsService;
    private HardwarePartRepository partRepository;

    @BeforeEach
    void setUp() {
        partRepository = mock(HardwarePartRepository.class);
        analyticsService = new AnalyticsService(partRepository);
    }

    @Test
    void getHardwareStockAnalyticsReturnsListOfCategoryStockDTO() {
        List<CategoryStockDTO> mockData = Arrays.asList(
                new CategoryStockDTO("GPU", 15L),
                new CategoryStockDTO("CPU", 42L)
        );
        when(partRepository.getStockPerCategory()).thenReturn(mockData);
        List<CategoryStockDTO> result = analyticsService.getHardwareStockAnalytics();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("GPU", result.get(0).getCategory());
        assertEquals(15L, result.get(0).getTotalStock());
        verify(partRepository, times(1)).getStockPerCategory();
    }
}