package com.tus.pcmanager.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.model.HardwarePart;
import com.tus.pcmanager.service.HardwarePartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

class HardwarePartControllerTest {

    private HardwarePartController hardwarePartController;
    private HardwarePartService hardwarePartService;

    @BeforeEach
    void setup() {
        hardwarePartService = mock(HardwarePartService.class);
        hardwarePartController = new HardwarePartController(hardwarePartService);
    }

    @Test
    void getAllPartsWithoutSearchReturnsOkAndList() {
        HardwarePart part1 = new HardwarePart(1L, "GPU", "NVIDIA", "Graphics", new BigDecimal("500"), 2);
        HardwarePart part2 = new HardwarePart(2L, "CPU", "Intel", "Processor", new BigDecimal("300"), 5);
        List<HardwarePart> expectedParts = Arrays.asList(part1, part2);
        when(hardwarePartService.getAllParts()).thenReturn(expectedParts);
        ResponseEntity<List<HardwarePart>> response = hardwarePartController.getAllParts(null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(hardwarePartService, times(1)).getAllParts();
        verify(hardwarePartService, never()).searchParts(anyString());
    }

    @Test
    void getAllPartsWithSearchReturnsOkAndFilteredList() {
        HardwarePart part1 = new HardwarePart(1L, "GPU", "NVIDIA", "Graphics", new BigDecimal("500"), 2);
        List<HardwarePart> expectedParts = Arrays.asList(part1);
        when(hardwarePartService.searchParts("NVIDIA")).thenReturn(expectedParts);
        ResponseEntity<List<HardwarePart>> response = hardwarePartController.getAllParts("NVIDIA");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("NVIDIA", response.getBody().get(0).getManufacturer());
        verify(hardwarePartService, times(1)).searchParts("NVIDIA");
        verify(hardwarePartService, never()).getAllParts();
    }

    @Test
    void getPartByIdReturnsOkAndPart() {
        HardwarePart mockPart = new HardwarePart(1L, "RAM", "Corsair", "Memory", new BigDecimal("100"), 4);
        when(hardwarePartService.getPartById(1L)).thenReturn(mockPart);
        ResponseEntity<HardwarePart> response = hardwarePartController.getPartById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RAM", response.getBody().getName());
        verify(hardwarePartService, times(1)).getPartById(1L);
    }

    @Test
    void addPartReturnsCreatedAndPart() {
        HardwarePart newPart = new HardwarePart(null, "Motherboard", "ASUS", "Board", new BigDecimal("250"), 3);
        HardwarePart savedPart = new HardwarePart(1L, "Motherboard", "ASUS", "Board", new BigDecimal("250"), 3);
        when(hardwarePartService.addPart(newPart)).thenReturn(savedPart);
        ResponseEntity<HardwarePart> response = hardwarePartController.addPart(newPart);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(hardwarePartService, times(1)).addPart(newPart);
    }

    @Test
    void updatePartReturnsOkAndUpdatedPart() {
        HardwarePart updateDetails = new HardwarePart(null, "Updated Motherboard", "ASUS", "Board", new BigDecimal("260"), 5);
        HardwarePart updatedPart = new HardwarePart(1L, "Updated Motherboard", "ASUS", "Board", new BigDecimal("260"), 5);
        when(hardwarePartService.updatePart(1L, updateDetails)).thenReturn(updatedPart);
        ResponseEntity<HardwarePart> response = hardwarePartController.updatePart(1L, updateDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Motherboard", response.getBody().getName());
        verify(hardwarePartService, times(1)).updatePart(1L, updateDetails);
    }

    @Test
    void deletePartReturnsNoContent() {
        doNothing().when(hardwarePartService).deletePart(1L);
        ResponseEntity<Void> response = hardwarePartController.deletePart(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(hardwarePartService, times(1)).deletePart(1L);
    }
}