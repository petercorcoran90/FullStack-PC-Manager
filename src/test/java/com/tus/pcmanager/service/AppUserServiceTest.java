package com.tus.pcmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tus.pcmanager.exception.UserAlreadyExistsException;
import com.tus.pcmanager.model.AppUser;
import com.tus.pcmanager.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AppUserServiceTest {

	private AppUserService appUserService;
	private AppUserRepository appUserRepository;
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		appUserRepository = mock(AppUserRepository.class);
		passwordEncoder = mock(PasswordEncoder.class);

		appUserService = new AppUserService(appUserRepository, passwordEncoder);
	}

	@Test
	void registerUserSuccess() {
		String username = "testuser";
		String plainPassword = "password123";
		String encodedPassword = "encodedPassword123";
		String role = "ROLE_USER";
		when(appUserRepository.existsByUsername(username)).thenReturn(false);
		when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
		AppUser savedUser = new AppUser();
		savedUser.setId(1L);
		savedUser.setUsername(username);
		savedUser.setPassword(encodedPassword);
		savedUser.setRole(role);
		when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser);
		AppUser result = appUserService.registerUser(username, plainPassword, role);
		assertNotNull(result);
		assertEquals(username, result.getUsername());
		assertEquals(encodedPassword, result.getPassword());
		verify(appUserRepository, times(1)).existsByUsername(username);
		verify(passwordEncoder, times(1)).encode(plainPassword);
		verify(appUserRepository, times(1)).save(any(AppUser.class));
	}

	@Test
	void registerUserAlreadyExistsThrowsException() {
		String username = "existinguser";
		when(appUserRepository.existsByUsername(username)).thenReturn(true);
		UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
				() -> appUserService.registerUser(username, "password", "ROLE_USER"));
		assertEquals("Username 'existinguser' is already taken.", ex.getMessage());
		verify(appUserRepository, never()).save(any(AppUser.class));
	}
}