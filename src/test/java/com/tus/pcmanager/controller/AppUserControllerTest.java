package com.tus.pcmanager.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.dto.AuthResponse;
import com.tus.pcmanager.dto.LoginRequest;
import com.tus.pcmanager.dto.RegisterRequest;
import com.tus.pcmanager.model.AppUser;
import com.tus.pcmanager.security.JwtUtils;
import com.tus.pcmanager.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class AppUserControllerTest {

	private AppUserController appUserController;
	private AppUserService appUserService;
	private AuthenticationManager authenticationManager;
	private JwtUtils jwtUtils;

	@BeforeEach
	void setup() {
		appUserService = mock(AppUserService.class);
		authenticationManager = mock(AuthenticationManager.class);
		jwtUtils = mock(JwtUtils.class);

		appUserController = new AppUserController(appUserService, authenticationManager, jwtUtils);
	}

	@Test
	void registerUserSuccess() {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("newuser");
		request.setPassword("password");
		request.setRole("ROLE_USER");
		AppUser mockUser = new AppUser();
		when(appUserService.registerUser(anyString(), anyString(), anyString())).thenReturn(mockUser);
		ResponseEntity<String> response = appUserController.registerUser(request);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("User registered successfully", response.getBody());
		verify(appUserService, times(1)).registerUser("newuser", "password", "ROLE_USER");
	}

	@Test
	void loginUserSuccessReturnsJwt() {
		LoginRequest request = new LoginRequest();
		request.setUsername("admin");
		request.setPassword("adminpass");
		String fakeJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakeToken";
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
		when(jwtUtils.generateToken("admin")).thenReturn(fakeJwtToken);
		ResponseEntity<Object> response = appUserController.loginUser(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		AuthResponse body = (AuthResponse) response.getBody();
		assertNotNull(body);
		assertEquals(fakeJwtToken, body.getToken());
		assertEquals("Login Successful", body.getMessage());
	}

	@Test
	void loginUserFailureReturnsUnauthorised() {
		LoginRequest request = new LoginRequest();
		request.setUsername("wronguser");
		request.setPassword("wrongpass");
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Bad credentials"));
		ResponseEntity<Object> response = appUserController.loginUser(request);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals("Invalid username or password.", response.getBody());
		verify(jwtUtils, never()).generateToken(anyString());
	}
}