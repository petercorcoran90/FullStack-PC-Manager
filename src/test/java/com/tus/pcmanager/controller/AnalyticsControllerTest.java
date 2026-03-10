package com.tus.pcmanager.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.dto.CategoryStockDTO;
import com.tus.pcmanager.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;

class AnalyticsControllerTest {

    private AnalyticsController analyticsController;
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = mock(AnalyticsService.class);
        analyticsController = new AnalyticsController(analyticsService);
    }

    @Test
    void getStockAnalyticsReturnsOkStatusAndData() {
        List<CategoryStockDTO> mockData = Arrays.asList(new CategoryStockDTO("RAM", 100L));
        when(analyticsService.getHardwareStockAnalytics()).thenReturn(mockData);
        ResponseEntity<List<CategoryStockDTO>> response = analyticsController.getStockAnalytics();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("RAM", response.getBody().get(0).getCategory());
        verify(analyticsService, times(1)).getHardwareStockAnalytics();
    }
}