package com.tus.pcmanager.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.dto.PcBuildDTO;
import com.tus.pcmanager.service.PcBuildService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

class PcBuildControllerTest {

    private PcBuildController pcBuildController;
    private PcBuildService pcBuildService;

    @BeforeEach
    void setup() {
        pcBuildService = mock(PcBuildService.class);
        pcBuildController = new PcBuildController(pcBuildService);
    }

    @Test
    void createBuildReturnsCreatedStatus() {
        PcBuildDTO dto = PcBuildDTO.builder().id(1L).buildName("Office PC").build();
        when(pcBuildService.createBuild("Office PC", "user1")).thenReturn(dto);
        ResponseEntity<PcBuildDTO> response = pcBuildController.createBuild("user1", "Office PC");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Office PC", response.getBody().getBuildName());
        verify(pcBuildService, times(1)).createBuild("Office PC", "user1");
    }

    @Test
    void getUserBuildsReturnsOkAndList() {
        PcBuildDTO dto = PcBuildDTO.builder().id(1L).buildName("My PC").build();
        when(pcBuildService.getBuildsForUser("user1")).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<PcBuildDTO>> response = pcBuildController.getUserBuilds("user1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void addPartToBuildReturnsUpdatedBuild() {
        PcBuildDTO dto = PcBuildDTO.builder().id(1L).totalPrice(new BigDecimal("500.00")).build();
        when(pcBuildService.addPartToBuild(1L, 10L)).thenReturn(dto);
        ResponseEntity<PcBuildDTO> response = pcBuildController.addPartToBuild(1L, 10L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("500.00"), response.getBody().getTotalPrice());
        verify(pcBuildService, times(1)).addPartToBuild(1L, 10L);
    }

    @Test
    void removePartFromBuildReturnsOkOnSuccess() {
        PcBuildDTO dto = PcBuildDTO.builder().id(1L).build();
        when(pcBuildService.removePartFromBuild(1L, 10L)).thenReturn(dto);
        ResponseEntity<PcBuildDTO> response = pcBuildController.removePartFromBuild(1L, 10L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(pcBuildService, times(1)).removePartFromBuild(1L, 10L);
    }
    
    @Test
    void deleteBuildReturnsNoContent() {
        doNothing().when(pcBuildService).deleteBuild(1L);
        ResponseEntity<Void> response = pcBuildController.deleteBuild(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pcBuildService, times(1)).deleteBuild(1L);
    }
}