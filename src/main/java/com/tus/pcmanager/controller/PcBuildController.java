package com.tus.pcmanager.controller;

import com.tus.pcmanager.dto.PcBuildDto;
import com.tus.pcmanager.model.PcBuild;
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
    public ResponseEntity<List<PcBuildDto>> getUserBuilds(@PathVariable String username) {
        return ResponseEntity.ok(buildService.getBuildsForUser(username));
    }

    @PostMapping
    public ResponseEntity<PcBuild> createBuild(@RequestParam String username, @RequestParam String name) {
        PcBuild newBuild = buildService.createBuild(name, username);
        return new ResponseEntity<>(newBuild, HttpStatus.CREATED);
    }

    @PostMapping("/{buildId}/parts/{partId}")
    public ResponseEntity<Void> addPartToBuild(@PathVariable Long buildId, @PathVariable Long partId) {
        buildService.addPartToBuild(buildId, partId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{buildId}/parts/{partId}")
    public ResponseEntity<Void> removePartFromBuild(@PathVariable Long buildId, @PathVariable Long partId) {
        buildService.removePartFromBuild(buildId, partId);
        return ResponseEntity.noContent().build();
    }
}