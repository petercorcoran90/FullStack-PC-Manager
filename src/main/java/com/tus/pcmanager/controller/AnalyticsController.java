package com.tus.pcmanager.controller;

import com.tus.pcmanager.dto.CategoryStockDTO;
import com.tus.pcmanager.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/stock")
    public ResponseEntity<List<CategoryStockDTO>> getStockAnalytics() {
        return ResponseEntity.ok(analyticsService.getHardwareStockAnalytics());
    }
}