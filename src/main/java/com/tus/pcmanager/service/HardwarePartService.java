package com.tus.pcmanager.service;

import com.tus.pcmanager.dto.HardwarePartDTO;
import com.tus.pcmanager.exception.DuplicateResourceException;
import com.tus.pcmanager.exception.InvalidResourceException;
import com.tus.pcmanager.exception.ResourceNotFoundException;
import com.tus.pcmanager.model.HardwarePart;
import com.tus.pcmanager.repository.HardwarePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	private HardwarePartDTO mapToDTO(HardwarePart part) {
		return HardwarePartDTO.builder().id(part.getId()).name(part.getName()).manufacturer(part.getManufacturer())
				.category(part.getCategory()).price(part.getPrice()).stockLevel(part.getStockLevel()).build();
	}

	public HardwarePart getPartById(Long id) {
		Optional<HardwarePart> part = partRepository.findById(id);
		if (part.isPresent()) {
			return part.get();
		}
		throw new ResourceNotFoundException("Hardware Part not found with ID: " + id);
	}

	public HardwarePart addPart(HardwarePart part) {
		if (partRepository.existsByNameIgnoreCase(part.getName())) {
			throw new DuplicateResourceException("A part with the name '" + part.getName() + "' already exists.");
		}
		validatePart(part);
		return partRepository.save(part);
	}

	public HardwarePart updatePart(Long id, HardwarePart partDetails) {
		HardwarePart existingPart = getPartById(id);
		validatePart(partDetails);
		existingPart.setName(partDetails.getName());
		existingPart.setManufacturer(partDetails.getManufacturer());
		existingPart.setCategory(partDetails.getCategory());
		existingPart.setPrice(partDetails.getPrice());
		existingPart.setStockLevel(partDetails.getStockLevel());
		return partRepository.save(existingPart);
	}

	public void deletePart(Long id) {
		HardwarePart existingPart = getPartById(id);
		partRepository.delete(existingPart);
	}

	private void validatePart(HardwarePart part) {
		if (part.getPrice() == null || part.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidResourceException("Price cannot be negative or null.");
		}
		if (part.getStockLevel() == null || part.getStockLevel() < 0) {
			throw new InvalidResourceException("Stock level cannot be negative or null.");
		}
	}
}