package com.tus.pcmanager.controller;

import com.tus.pcmanager.dto.AuthResponse;
import com.tus.pcmanager.dto.LoginRequest;
import com.tus.pcmanager.dto.RegisterRequest;
import com.tus.pcmanager.security.JwtUtils;
import com.tus.pcmanager.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {

	private final AppUserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;

	private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
		userService.registerUser(request.getUsername(), request.getPassword(), request.getRole());
		return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<Object> loginUser(@RequestBody LoginRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			String token = jwtUtils.generateToken(request.getUsername());
			return ResponseEntity.ok(new AuthResponse(token, "Login Successful"));

		} catch (Exception e) {
			logger.warn("Failed login attempt for user: {}", request.getUsername());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
		}
	}
}