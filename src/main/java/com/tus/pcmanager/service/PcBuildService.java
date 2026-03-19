package com.tus.pcmanager.service;

import com.tus.pcmanager.dto.HardwarePartDTO;
import com.tus.pcmanager.dto.PcBuildDTO;
import com.tus.pcmanager.exception.ResourceNotFoundException;
import com.tus.pcmanager.model.AppUser;
import com.tus.pcmanager.model.HardwarePart;
import com.tus.pcmanager.model.PcBuild;
import com.tus.pcmanager.repository.AppUserRepository;
import com.tus.pcmanager.repository.HardwarePartRepository;
import com.tus.pcmanager.repository.PcBuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PcBuildService {

	private final PcBuildRepository buildRepository;
	private final HardwarePartRepository partRepository;
	private final AppUserRepository userRepository;

	@Transactional
	public PcBuildDTO createBuild(String buildName, String username) {
		AppUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

		PcBuild newBuild = new PcBuild();
		newBuild.setBuildName(buildName);
		newBuild.setUser(user);

		return mapToDTO(buildRepository.save(newBuild));
	}

	@Transactional
	public PcBuildDTO addPartToBuild(Long buildId, Long partId) {
		PcBuild build = buildRepository.findById(buildId)
				.orElseThrow(() -> new ResourceNotFoundException("Build not found: " + buildId));

		HardwarePart part = partRepository.findById(partId)
				.orElseThrow(() -> new ResourceNotFoundException("Part not found: " + partId));

		build.getParts().add(part);
		return mapToDTO(buildRepository.save(build));
	}

	@Transactional
	public PcBuildDTO removePartFromBuild(Long buildId, Long partId) {
		PcBuild build = buildRepository.findById(buildId)
				.orElseThrow(() -> new ResourceNotFoundException("Build not found: " + buildId));

		HardwarePart partToRemove = build.getParts().stream().filter(p -> p.getId().equals(partId)).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Part not found in this build."));

		build.getParts().remove(partToRemove);
		return mapToDTO(buildRepository.save(build));
	}

	public List<PcBuildDTO> getBuildsForUser(String username) {
		AppUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

		return buildRepository.findByUserId(user.getId()).stream().map(this::mapToDTO).toList();
	}

	private PcBuildDTO mapToDTO(PcBuild build) {
		List<HardwarePartDTO> partDtos = build.getParts().stream()
				.map(part -> HardwarePartDTO.builder().id(part.getId()).name(part.getName())
						.manufacturer(part.getManufacturer()).category(part.getCategory()).price(part.getPrice())
						.stockLevel(part.getStockLevel()).build())
				.toList();

		return PcBuildDTO.builder().id(build.getId()).buildName(build.getBuildName()).createdAt(build.getCreatedAt())
				.totalPrice(build.calculateTotalPrice()).parts(partDtos).build();
	}

	@Transactional
	public void deleteBuild(Long buildId) {
		if (!buildRepository.existsById(buildId)) {
			throw new ResourceNotFoundException("Build not found: " + buildId);
		}
		buildRepository.deleteById(buildId);
	}
}