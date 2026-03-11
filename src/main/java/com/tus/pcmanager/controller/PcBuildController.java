package com.tus.pcmanager.controller;

import com.tus.pcmanager.dto.PcBuildDTO;
import com.tus.pcmanager.service.PcBuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
public class PcBuildController {

    private final PcBuildService buildService;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PcBuildDTO>> getUserBuilds(@PathVariable String username) {
        return ResponseEntity.ok(buildService.getBuildsForUser(username));
    }

    @PostMapping
    public ResponseEntity<PcBuildDTO> createBuild(@RequestParam String username, @RequestParam String name) {
        return new ResponseEntity<>(buildService.createBuild(name, username), HttpStatus.CREATED);
    }

    @PostMapping("/{buildId}/parts/{partId}")
    public ResponseEntity<PcBuildDTO> addPartToBuild(@PathVariable Long buildId, @PathVariable Long partId) {
        return ResponseEntity.ok(buildService.addPartToBuild(buildId, partId));
    }

    @DeleteMapping("/{buildId}/parts/{partId}")
    public ResponseEntity<PcBuildDTO> removePartFromBuild(@PathVariable Long buildId, @PathVariable Long partId) {
        return ResponseEntity.ok(buildService.removePartFromBuild(buildId, partId));
    
    }
    
    @DeleteMapping("/{buildId}")
    public ResponseEntity<Void> deleteBuild(@PathVariable Long buildId) {
        buildService.deleteBuild(buildId);
        return ResponseEntity.noContent().build();
    }
}