package com.tus.pcmanager.service;

import com.tus.pcmanager.dto.HardwarePartDTO;
import com.tus.pcmanager.exception.DuplicateResourceException;
import com.tus.pcmanager.exception.ResourceNotFoundException;
import com.tus.pcmanager.model.HardwarePart;
import com.tus.pcmanager.repository.HardwarePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HardwarePartService {

    private final HardwarePartRepository partRepository;

    public List<HardwarePartDTO> getAllParts() {
        List<HardwarePart> parts = partRepository.findAll();
        List<HardwarePartDTO> partDTOs = new ArrayList<>();
        for (HardwarePart part : parts) {
            partDTOs.add(mapToDTO(part));
        }
        return partDTOs;
    }

    public List<HardwarePartDTO> searchParts(String keyword) {
        List<HardwarePart> parts;
        if (keyword != null && !keyword.isEmpty()) {
            parts = partRepository.findByNameContainingIgnoreCase(keyword);
        } else {
            parts = partRepository.findAll();
        }

        List<HardwarePartDTO> partDTOs = new ArrayList<>();
        for (HardwarePart part : parts) {
            partDTOs.add(mapToDTO(part));
        }
        return partDTOs;
    }

    // Helper method to get the raw Entity for internal use
    private HardwarePart getPartEntityById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hardware Part not found with ID: " + id));
    }

    public HardwarePartDTO getPartById(Long id) {
        return mapToDTO(getPartEntityById(id));
    }

    public HardwarePartDTO addPart(HardwarePartDTO partDto) {
        if (partRepository.existsByNameIgnoreCase(partDto.getName())) {
            throw new DuplicateResourceException("A part with the name '" + partDto.getName() + "' already exists.");
        }
        
        HardwarePart part = new HardwarePart();
        part.setName(partDto.getName());
        part.setManufacturer(partDto.getManufacturer());
        part.setCategory(partDto.getCategory());
        part.setPrice(partDto.getPrice());
        part.setStockLevel(partDto.getStockLevel());
        
        return mapToDTO(partRepository.save(part));
    }

    public HardwarePartDTO updatePart(Long id, HardwarePartDTO partDto) {
        HardwarePart existingPart = getPartEntityById(id);
        
        existingPart.setName(partDto.getName());
        existingPart.setManufacturer(partDto.getManufacturer());
        existingPart.setCategory(partDto.getCategory());
        existingPart.setPrice(partDto.getPrice());
        existingPart.setStockLevel(partDto.getStockLevel());
        
        return mapToDTO(partRepository.save(existingPart));
    }

    public void deletePart(Long id) {
        HardwarePart existingPart = getPartEntityById(id);
        partRepository.delete(existingPart);
    }

    private HardwarePartDTO mapToDTO(HardwarePart part) {
        return HardwarePartDTO.builder()
                .id(part.getId())
                .name(part.getName())
                .manufacturer(part.getManufacturer())
                .category(part.getCategory())
                .price(part.getPrice())
                .stockLevel(part.getStockLevel())
                .build();
    }
}