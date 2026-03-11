package com.tus.pcmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.dto.PcBuildDTO;
import com.tus.pcmanager.exception.ResourceNotFoundException;
import com.tus.pcmanager.model.AppUser;
import com.tus.pcmanager.model.HardwarePart;
import com.tus.pcmanager.model.PcBuild;
import com.tus.pcmanager.repository.AppUserRepository;
import com.tus.pcmanager.repository.HardwarePartRepository;
import com.tus.pcmanager.repository.PcBuildRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class PcBuildServiceTest {

	private PcBuildService pcBuildService;
	private PcBuildRepository buildRepository;
	private HardwarePartRepository partRepository;
	private AppUserRepository userRepository;

	@BeforeEach
	void setup() {
		buildRepository = mock(PcBuildRepository.class);
		partRepository = mock(HardwarePartRepository.class);
		userRepository = mock(AppUserRepository.class);
		pcBuildService = new PcBuildService(buildRepository, partRepository, userRepository);
	}

	@Test
	void createBuildSuccessReturnsPcBuildDTO() {
		AppUser user = new AppUser(1L, "tester", "password", "ROLE_USER", new ArrayList<>());
		PcBuild savedBuild = new PcBuild(1L, "Gaming PC", LocalDateTime.now(), user, new ArrayList<>());
		when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));
		when(buildRepository.save(any(PcBuild.class))).thenReturn(savedBuild);
		PcBuildDTO result = pcBuildService.createBuild("Gaming PC", "tester");
		assertNotNull(result);
		assertEquals("Gaming PC", result.getBuildName());
		verify(buildRepository, times(1)).save(any(PcBuild.class));
	}

	@Test
	void addPartToBuildSuccessCalculatesNewTotal() {
		HardwarePart gpu = new HardwarePart(10L, "RTX 4080", "NVIDIA", "GPU", new BigDecimal("1200.00"), 5);
		PcBuild build = new PcBuild(1L, "High End", LocalDateTime.now(), new AppUser(), new ArrayList<>());
		when(buildRepository.findById(1L)).thenReturn(Optional.of(build));
		when(partRepository.findById(10L)).thenReturn(Optional.of(gpu));
		when(buildRepository.save(any(PcBuild.class))).thenReturn(build);
		PcBuildDTO result = pcBuildService.addPartToBuild(1L, 10L);
		assertEquals(1, result.getParts().size());
		assertEquals(new BigDecimal("1200.00"), result.getTotalPrice());
		verify(buildRepository, times(1)).save(build);
	}

	@Test
	void removePartFromBuildWithDuplicatesOnlyRemovesOne() {
		HardwarePart ram = new HardwarePart(20L, "16GB RAM", "Corsair", "RAM", new BigDecimal("80.00"), 10);
		List<HardwarePart> parts = new ArrayList<>(Arrays.asList(ram, ram));
		PcBuild build = new PcBuild(1L, "Workstation", LocalDateTime.now(), new AppUser(), parts);
		when(buildRepository.findById(1L)).thenReturn(Optional.of(build));
		when(buildRepository.save(any(PcBuild.class))).thenReturn(build);
		PcBuildDTO result = pcBuildService.removePartFromBuild(1L, 20L);
		assertEquals(1, result.getParts().size());
		assertEquals(new BigDecimal("80.00"), result.getTotalPrice());
		verify(buildRepository, times(1)).save(build);
	}

	@Test
	void getBuildsForUserReturnsMappedDTOList() {
		AppUser user = new AppUser(1L, "tester", "pass", "ROLE_USER", new ArrayList<>());
		PcBuild b1 = new PcBuild(1L, "Build 1", LocalDateTime.now(), user, new ArrayList<>());
		when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));
		when(buildRepository.findByUserId(1L)).thenReturn(Arrays.asList(b1));
		List<PcBuildDTO> results = pcBuildService.getBuildsForUser("tester");
		assertEquals(1, results.size());
		assertEquals("Build 1", results.get(0).getBuildName());
	}

	@Test
	void createBuildUserNotFoundThrowsException() {
		when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> pcBuildService.createBuild("PC", "unknown"));
	}

	@Test
	void addPartToBuildBuildNotFoundThrowsException() {
		when(buildRepository.findById(99L)).thenReturn(Optional.empty());
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> pcBuildService.addPartToBuild(99L, 1L));
		assertEquals("Build not found: 99", ex.getMessage());
	}

	@Test
	void addPartToBuildPartNotFoundThrowsException() {
		PcBuild mockBuild = new PcBuild();
		when(buildRepository.findById(1L)).thenReturn(Optional.of(mockBuild));
		when(partRepository.findById(99L)).thenReturn(Optional.empty());
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> pcBuildService.addPartToBuild(1L, 99L));
		assertEquals("Part not found: 99", ex.getMessage());
	}

	@Test
	void removePartFromBuildBuildNotFoundThrowsException() {
		when(buildRepository.findById(99L)).thenReturn(Optional.empty());
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> pcBuildService.removePartFromBuild(99L, 1L));
		assertEquals("Build not found: 99", ex.getMessage());
	}

	@Test
	void getBuildsForUserUserNotFoundThrowsException() {
		when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> pcBuildService.getBuildsForUser("ghost"));
		assertEquals("User not found: ghost", ex.getMessage());
	}

	@Test
	void removePartFromBuildPartNotInBuildThrowsException() {
		HardwarePart existingPart = new HardwarePart();
		existingPart.setId(10L);
		List<HardwarePart> partsList = new ArrayList<>();
		partsList.add(existingPart);
		PcBuild mockBuild = new PcBuild();
		mockBuild.setId(1L);
		mockBuild.setParts(partsList);
		when(buildRepository.findById(1L)).thenReturn(Optional.of(mockBuild));
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
				() -> pcBuildService.removePartFromBuild(1L, 99L));
		assertEquals("Part not found in this build.", ex.getMessage());
		verify(buildRepository, never()).save(any(PcBuild.class));
	}
	
	@Test
	void deleteBuildSuccess() {
		when(buildRepository.existsById(1L)).thenReturn(true);
		pcBuildService.deleteBuild(1L);
		verify(buildRepository, times(1)).deleteById(1L);
	}

	@Test
	void deleteBuildNotFoundThrowsException() {
		when(buildRepository.existsById(99L)).thenReturn(false);
		ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
				() -> pcBuildService.deleteBuild(99L));
		assertEquals("Build not found: 99", ex.getMessage());
	}
}