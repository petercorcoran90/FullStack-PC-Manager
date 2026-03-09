package com.tus.pcmanager.controller;

import com.tus.pcmanager.model.HardwarePart;
import com.tus.pcmanager.service.HardwarePartService;
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
    public ResponseEntity<List<HardwarePart>> getAllParts(@RequestParam(required = false) String search) {
        if (search != null) {
            return ResponseEntity.ok(partService.searchParts(search));
        }
        return ResponseEntity.ok(partService.getAllParts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HardwarePart> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.getPartById(id));
    }

    @PostMapping
    public ResponseEntity<HardwarePart> addPart(@RequestBody HardwarePart part) {
        HardwarePart createdPart = partService.addPart(part);
        return new ResponseEntity<>(createdPart, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HardwarePart> updatePart(@PathVariable Long id, @RequestBody HardwarePart partDetails) {
        return ResponseEntity.ok(partService.updatePart(id, partDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}