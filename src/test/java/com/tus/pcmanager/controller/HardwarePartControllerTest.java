package com.tus.pcmanager.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.dto.HardwarePartDTO;
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
		HardwarePartDTO part1 = new HardwarePartDTO(1L, "GPU", "NVIDIA", "Graphics", new BigDecimal("500"), 2);
		HardwarePartDTO part2 = new HardwarePartDTO(2L, "CPU", "Intel", "Processor", new BigDecimal("300"), 5);
		List<HardwarePartDTO> expectedParts = Arrays.asList(part1, part2);
		when(hardwarePartService.getAllParts()).thenReturn(expectedParts);
		
		ResponseEntity<List<HardwarePartDTO>> response = hardwarePartController.getAllParts(null);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());
		verify(hardwarePartService, times(1)).getAllParts();
		verify(hardwarePartService, never()).searchParts(anyString());
	}

	@Test
	void getAllPartsWithSearchReturnsOkAndFilteredList() {
		HardwarePartDTO part1 = new HardwarePartDTO(1L, "GPU", "NVIDIA", "Graphics", new BigDecimal("500"), 2);
		List<HardwarePartDTO> expectedParts = Arrays.asList(part1);
		when(hardwarePartService.searchParts("NVIDIA")).thenReturn(expectedParts);
		
		ResponseEntity<List<HardwarePartDTO>> response = hardwarePartController.getAllParts("NVIDIA");
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals("NVIDIA", response.getBody().get(0).getManufacturer());
		verify(hardwarePartService, times(1)).searchParts("NVIDIA");
		verify(hardwarePartService, never()).getAllParts();
	}

	@Test
	void getPartByIdReturnsOkAndPart() {
		HardwarePartDTO mockPart = new HardwarePartDTO(1L, "RAM", "Corsair", "Memory", new BigDecimal("100"), 4);
		when(hardwarePartService.getPartById(1L)).thenReturn(mockPart);
		
		ResponseEntity<HardwarePartDTO> response = hardwarePartController.getPartById(1L);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("RAM", response.getBody().getName());
		verify(hardwarePartService, times(1)).getPartById(1L);
	}

	@Test
	void addPartReturnsCreatedAndPart() {
		HardwarePartDTO newPart = new HardwarePartDTO(null, "Motherboard", "ASUS", "Board", new BigDecimal("250"), 3);
		HardwarePartDTO savedPart = new HardwarePartDTO(1L, "Motherboard", "ASUS", "Board", new BigDecimal("250"), 3);
		when(hardwarePartService.addPart(newPart)).thenReturn(savedPart);
		
		ResponseEntity<HardwarePartDTO> response = hardwarePartController.addPart(newPart);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1L, response.getBody().getId());
		verify(hardwarePartService, times(1)).addPart(newPart);
	}

	@Test
	void updatePartReturnsOkAndUpdatedPart() {
		HardwarePartDTO updateDetails = new HardwarePartDTO(null, "Updated Motherboard", "ASUS", "Board", new BigDecimal("260"), 5);
		HardwarePartDTO updatedPart = new HardwarePartDTO(1L, "Updated Motherboard", "ASUS", "Board", new BigDecimal("260"), 5);
		when(hardwarePartService.updatePart(1L, updateDetails)).thenReturn(updatedPart);
		
		ResponseEntity<HardwarePartDTO> response = hardwarePartController.updatePart(1L, updateDetails);
		
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