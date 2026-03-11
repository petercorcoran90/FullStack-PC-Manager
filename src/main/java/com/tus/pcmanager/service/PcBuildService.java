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
import java.util.ArrayList;
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

        HardwarePart partToRemove = null;
        for (HardwarePart p : build.getParts()) {
            if (p.getId().equals(partId)) {
                partToRemove = p;
                break;
            }
        }

        if (partToRemove != null) {
            build.getParts().remove(partToRemove);
            return mapToDTO(buildRepository.save(build));
        } else {
            throw new ResourceNotFoundException("Part not found in this build.");
        }
    }

    public List<PcBuildDTO> getBuildsForUser(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<PcBuild> builds = buildRepository.findByUserId(user.getId());
        List<PcBuildDTO> dtos = new ArrayList<>();
        for (PcBuild build : builds) {
            dtos.add(mapToDTO(build));
        }
        return dtos;
    }

    private PcBuildDTO mapToDTO(PcBuild build) {
        List<HardwarePartDTO> partDtos = new ArrayList<>();
        for (HardwarePart part : build.getParts()) {
            partDtos.add(HardwarePartDTO.builder()
                    .id(part.getId())
                    .name(part.getName())
                    .manufacturer(part.getManufacturer())
                    .category(part.getCategory())
                    .price(part.getPrice()) 
                    .stockLevel(part.getStockLevel())
                    .build());
        }

        return PcBuildDTO.builder()
                .id(build.getId())
                .buildName(build.getBuildName())
                .createdAt(build.getCreatedAt())
                .totalPrice(build.calculateTotalPrice()) 
                .parts(partDtos)
                .build();
    }
    
    @Transactional
    public void deleteBuild(Long buildId) {
        if (!buildRepository.existsById(buildId)) {
            throw new ResourceNotFoundException("Build not found: " + buildId);
        }
        buildRepository.deleteById(buildId);
    }
}