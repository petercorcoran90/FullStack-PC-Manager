package com.tus.pcmanager.controller;

import com.tus.pcmanager.dto.RegisterRequest;
import com.tus.pcmanager.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        userService.registerUser(request.getUsername(), request.getPassword(), request.getRole());
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }
}