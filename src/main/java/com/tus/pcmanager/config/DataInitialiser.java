package com.tus.pcmanager.config;

import com.tus.pcmanager.repository.AppUserRepository;
import com.tus.pcmanager.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitialiser implements CommandLineRunner {

    private final AppUserService userService;
    private final AppUserRepository userRepository;

    private static final String ADMIN_USER = "admin";
    private static final String TEST_USER = "user";

    @Override
    public void run(String... args) throws Exception {
        
        if (!Boolean.TRUE.equals(userRepository.existsByUsername(ADMIN_USER))) {
            userService.registerUser(ADMIN_USER, ADMIN_USER, "ROLE_ADMIN");
            log.info("ADMIN USER CREATED (Username: {} / Password: {})", ADMIN_USER, ADMIN_USER);
        }

        if (!Boolean.TRUE.equals(userRepository.existsByUsername(TEST_USER))) {
            userService.registerUser(TEST_USER, TEST_USER, "ROLE_USER");
            log.info("TEST USER CREATED (Username: {} / Password: {})", TEST_USER, TEST_USER);
        }
    }
}