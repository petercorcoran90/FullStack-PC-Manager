package com.tus.pcmanager.controller;

import com.tus.pcmanager.dto.HardwarePartDTO;
import com.tus.pcmanager.service.HardwarePartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class HardwarePartController {

    private final HardwarePartService partService;

    @GetMapping
    public ResponseEntity<List<HardwarePartDTO>> getAllParts(@RequestParam(required = false) String search) {
        List<HardwarePartDTO> parts;
        if (search != null && !search.isEmpty()) {
            parts = partService.searchParts(search);
        } else {
            parts = partService.getAllParts();
        }
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HardwarePartDTO> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.getPartById(id));
    }

    @PostMapping
    public ResponseEntity<HardwarePartDTO> addPart(@Valid @RequestBody HardwarePartDTO partDto) {
        HardwarePartDTO createdPart = partService.addPart(partDto);
        return new ResponseEntity<>(createdPart, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HardwarePartDTO> updatePart(@PathVariable Long id, @Valid @RequestBody HardwarePartDTO partDto) {
        return ResponseEntity.ok(partService.updatePart(id, partDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}