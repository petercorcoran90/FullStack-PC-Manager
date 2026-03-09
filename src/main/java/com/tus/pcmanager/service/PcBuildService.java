package com.tus.pcmanager.service;

import com.tus.pcmanager.dto.HardwarePartDTO;
import com.tus.pcmanager.dto.PcBuildDto;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PcBuildService {

	private final PcBuildRepository buildRepository;
	private final HardwarePartRepository partRepository;
	private final AppUserRepository userRepository;

	public PcBuild createBuild(String buildName, String username) {
		AppUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

		PcBuild newBuild = new PcBuild();
		newBuild.setBuildName(buildName);
		newBuild.setUser(user);
		newBuild.setCreatedAt(LocalDateTime.now());

		return buildRepository.save(newBuild);
	}

	@Transactional
	public void addPartToBuild(Long buildId, Long partId) {
		PcBuild build = buildRepository.findById(buildId)
				.orElseThrow(() -> new ResourceNotFoundException("Build not found with ID: " + buildId));

		HardwarePart part = partRepository.findById(partId)
				.orElseThrow(() -> new ResourceNotFoundException("Hardware Part not found with ID: " + partId));

		build.getParts().add(part);

		buildRepository.save(build);
	}

	@Transactional
	public void removePartFromBuild(Long buildId, Long partId) {
		PcBuild build = buildRepository.findById(buildId)
				.orElseThrow(() -> new ResourceNotFoundException("Build not found with ID: " + buildId));

		HardwarePart partToRemove = partRepository.findById(partId)
				.orElseThrow(() -> new ResourceNotFoundException("Hardware Part not found with ID: " + partId));

		build.getParts().remove(partToRemove);
		buildRepository.save(build);
	}

	public List<PcBuildDto> getBuildsForUser(String username) {
		AppUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

		List<PcBuild> builds = buildRepository.findByUserId(user.getId());
		List<PcBuildDto> dtos = new ArrayList<>();

		for (PcBuild build : builds) {
			PcBuildDto dto = new PcBuildDto();
			dto.setId(build.getId());
			dto.setBuildName(build.getBuildName());
			dto.setCreatedAt(build.getCreatedAt());
			dto.setTotalPrice(build.getTotalPrice());

			List<HardwarePartDTO> partDtos = new ArrayList<>();
			for (HardwarePart part : build.getParts()) {
				HardwarePartDTO partDto = new HardwarePartDTO();
				partDto.setId(part.getId());
				partDto.setName(part.getName());
				partDto.setManufacturer(part.getManufacturer());
				partDto.setCategory(part.getCategory());
				partDto.setPrice(part.getPrice());
				partDto.setStockLevel(part.getStockLevel());
				partDtos.add(partDto);
			}
			dto.setParts(partDtos);

			dtos.add(dto);
		}
		return dtos;
	}
}