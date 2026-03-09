package com.tus.pcmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.exception.DuplicateResourceException;
import com.tus.pcmanager.exception.InvalidResourceException;
import com.tus.pcmanager.exception.ResourceNotFoundException;
import com.tus.pcmanager.model.HardwarePart;
import com.tus.pcmanager.repository.HardwarePartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class HardwarePartServiceTest {

    private HardwarePartService hardwarePartService;
    private HardwarePartRepository hardwarePartRepository;

    @BeforeEach
    void setup() {
        hardwarePartRepository = mock(HardwarePartRepository.class);
        hardwarePartService = new HardwarePartService(hardwarePartRepository);
    }

    @Test
    void addPartSuccess() {
        HardwarePart newPart = new HardwarePart(null, "Core i9", "Intel", "CPU", new BigDecimal("500.00"), 10);
        HardwarePart savedPart = new HardwarePart(1L, "Core i9", "Intel", "CPU", new BigDecimal("500.00"), 10);
        when(hardwarePartRepository.existsByNameIgnoreCase("Core i9")).thenReturn(false);
        when(hardwarePartRepository.save(any(HardwarePart.class))).thenReturn(savedPart);
        HardwarePart result = hardwarePartService.addPart(newPart);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Core i9", result.getName());
        verify(hardwarePartRepository, times(1)).existsByNameIgnoreCase("Core i9");
        verify(hardwarePartRepository, times(1)).save(newPart);
    }

    @Test
    void addPartDuplicateThrowsException() {
        HardwarePart duplicatePart = new HardwarePart(null, "Core i9", "Intel", "CPU", new BigDecimal("500.00"), 10);
        when(hardwarePartRepository.existsByNameIgnoreCase("Core i9")).thenReturn(true);
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, 
                () -> hardwarePartService.addPart(duplicatePart));
        assertEquals("A part with the name 'Core i9' already exists.", ex.getMessage());
        verify(hardwarePartRepository, never()).save(any(HardwarePart.class));
    }

    @Test
    void addPartNegativePriceThrowsException() {
        HardwarePart invalidPart = new HardwarePart(null, "Cheap CPU", "AMD", "CPU", new BigDecimal("-10.00"), 5);
        when(hardwarePartRepository.existsByNameIgnoreCase("Cheap CPU")).thenReturn(false);
        InvalidResourceException ex = assertThrows(InvalidResourceException.class, 
                () -> hardwarePartService.addPart(invalidPart));
        assertEquals("Price cannot be negative or null.", ex.getMessage());
        verify(hardwarePartRepository, never()).save(any(HardwarePart.class));
    }

    @Test
    void addPartNegativeStockThrowsException() {
        HardwarePart invalidPart = new HardwarePart(null, "Rare RAM", "Corsair", "RAM", new BigDecimal("100.00"), -5);
        when(hardwarePartRepository.existsByNameIgnoreCase("Rare RAM")).thenReturn(false);
        InvalidResourceException ex = assertThrows(InvalidResourceException.class, 
                () -> hardwarePartService.addPart(invalidPart));
        assertEquals("Stock level cannot be negative or null.", ex.getMessage());
        verify(hardwarePartRepository, never()).save(any(HardwarePart.class));
    }

    @Test
    void getPartByIdSuccess() {
        HardwarePart mockPart = new HardwarePart(1L, "RTX 4090", "NVIDIA", "GPU", new BigDecimal("1500.00"), 2);
        when(hardwarePartRepository.findById(1L)).thenReturn(Optional.of(mockPart));
        HardwarePart result = hardwarePartService.getPartById(1L);
        assertNotNull(result);
        assertEquals("RTX 4090", result.getName());
        verify(hardwarePartRepository, times(1)).findById(1L);
    }

    @Test
    void getPartByIdNotFoundThrowsException() {
        when(hardwarePartRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
                () -> hardwarePartService.getPartById(99L));
        assertEquals("Hardware Part not found with ID: 99", ex.getMessage());
    }

    @Test
    void searchPartsWithKeywordReturnsFilteredList() {
        HardwarePart part1 = new HardwarePart(1L, "Intel Core i5", "Intel", "CPU", new BigDecimal("200"), 5);
        HardwarePart part2 = new HardwarePart(2L, "Intel Core i7", "Intel", "CPU", new BigDecimal("300"), 5);
        when(hardwarePartRepository.findByNameContainingIgnoreCase("Intel")).thenReturn(Arrays.asList(part1, part2));
        List<HardwarePart> results = hardwarePartService.searchParts("Intel");
        assertEquals(2, results.size());
        verify(hardwarePartRepository, times(1)).findByNameContainingIgnoreCase("Intel");
        verify(hardwarePartRepository, never()).findAll();
    }

    @Test
    void searchPartsWithEmptyKeywordReturnsAll() {
        HardwarePart part1 = new HardwarePart(1L, "Intel Core i5", "Intel", "CPU", new BigDecimal("200"), 5);
        when(hardwarePartRepository.findAll()).thenReturn(Arrays.asList(part1));
        List<HardwarePart> results = hardwarePartService.searchParts("");
        assertEquals(1, results.size());
        verify(hardwarePartRepository, times(1)).findAll();
        verify(hardwarePartRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void updatePartSuccess() {
        HardwarePart existingPart = new HardwarePart(1L, "Old Name", "Brand", "Case", new BigDecimal("50.00"), 5);
        HardwarePart updateDetails = new HardwarePart(null, "New Name", "Brand", "Case", new BigDecimal("60.00"), 10);
        when(hardwarePartRepository.findById(1L)).thenReturn(Optional.of(existingPart));
        when(hardwarePartRepository.save(any(HardwarePart.class))).thenReturn(existingPart); // Returns the mutated object
        HardwarePart result = hardwarePartService.updatePart(1L, updateDetails);
        assertEquals("New Name", result.getName());
        assertEquals(new BigDecimal("60.00"), result.getPrice());
        assertEquals(10, result.getStockLevel());
        verify(hardwarePartRepository, times(1)).save(existingPart);
    }

    @Test
    void deletePartSuccess() {
        HardwarePart existingPart = new HardwarePart(1L, "Mouse", "Logitech", "Peripherals", new BigDecimal("20.00"), 5);
        when(hardwarePartRepository.findById(1L)).thenReturn(Optional.of(existingPart));
        hardwarePartService.deletePart(1L);
        verify(hardwarePartRepository, times(1)).findById(1L);
        verify(hardwarePartRepository, times(1)).delete(existingPart);
    }
    
    @Test
    void getAllPartsReturnsList() {
        HardwarePart part1 = new HardwarePart(1L, "GPU", "NVIDIA", "Graphics", new BigDecimal("500"), 2);
        HardwarePart part2 = new HardwarePart(2L, "CPU", "Intel", "Processor", new BigDecimal("300"), 5);
        when(hardwarePartRepository.findAll()).thenReturn(Arrays.asList(part1, part2));
        List<HardwarePart> results = hardwarePartService.getAllParts();
        assertEquals(2, results.size());
        verify(hardwarePartRepository, times(1)).findAll();
    }

    @Test
    void searchPartsWithNullKeywordReturnsAll() {
        HardwarePart part1 = new HardwarePart(1L, "GPU", "NVIDIA", "Graphics", new BigDecimal("500"), 2);
        when(hardwarePartRepository.findAll()).thenReturn(Arrays.asList(part1));
        List<HardwarePart> results = hardwarePartService.searchParts(null);
        assertEquals(1, results.size());
        verify(hardwarePartRepository, times(1)).findAll();
        verify(hardwarePartRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void addPartNullPriceThrowsException() {
        HardwarePart invalidPart = new HardwarePart(null, "No Price CPU", "AMD", "CPU", null, 5);
        when(hardwarePartRepository.existsByNameIgnoreCase("No Price CPU")).thenReturn(false);
        InvalidResourceException ex = assertThrows(InvalidResourceException.class, 
                () -> hardwarePartService.addPart(invalidPart));
        assertEquals("Price cannot be negative or null.", ex.getMessage());
        verify(hardwarePartRepository, never()).save(any(HardwarePart.class));
    }

    @Test
    void addPartNullStockThrowsException() {
        HardwarePart invalidPart = new HardwarePart(null, "No Stock RAM", "Corsair", "RAM", new BigDecimal("100.00"), null);
        when(hardwarePartRepository.existsByNameIgnoreCase("No Stock RAM")).thenReturn(false);
        InvalidResourceException ex = assertThrows(InvalidResourceException.class, 
                () -> hardwarePartService.addPart(invalidPart));
        assertEquals("Stock level cannot be negative or null.", ex.getMessage());
        verify(hardwarePartRepository, never()).save(any(HardwarePart.class));
    }
}