package com.tus.pcmanager.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.tus.pcmanager.dto.AuthResponse;
import com.tus.pcmanager.dto.LoginRequest;
import com.tus.pcmanager.dto.RegisterRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AppUserIT {

	@Value(value = "${local.server.port}")
	private int port;

	@Test
	void registerUserIntegrationTest() {
		TestRestTemplate restTemplate = new TestRestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RegisterRequest requestDto = new RegisterRequest();
		requestDto.setUsername("integrationuser");
		requestDto.setPassword("password123");
		requestDto.setRole("ROLE_USER");
		HttpEntity<RegisterRequest> request = new HttpEntity<>(requestDto, headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/api/users/register",
				request, String.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("User registered successfully", response.getBody());
	}

	@Test
	@Sql({ "/testuser.sql" })
	void loginUserSuccessIntegrationTest() {
		TestRestTemplate restTemplate = new TestRestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		LoginRequest loginDto = new LoginRequest();
		loginDto.setUsername("sqluser");
		loginDto.setPassword("password");
		HttpEntity<LoginRequest> request = new HttpEntity<>(loginDto, headers);
		ResponseEntity<AuthResponse> response = restTemplate
				.postForEntity("http://localhost:" + port + "/api/users/login", request, AuthResponse.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getToken());
		assertEquals("Login Successful", response.getBody().getMessage());
	}

	@Test
	void loginUserFailureIntegrationTest() {
		TestRestTemplate restTemplate = new TestRestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		LoginRequest loginDto = new LoginRequest();
		loginDto.setUsername("wronguser");
		loginDto.setPassword("wrongpass");
		HttpEntity<LoginRequest> request = new HttpEntity<>(loginDto, headers);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/api/users/login",
				request, String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals("Invalid username or password.", response.getBody());
	}
}