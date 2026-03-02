package com.tus.pcmanager.config;

import com.tus.pcmanager.repository.AppUserRepository;
import com.tus.pcmanager.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitialiser implements CommandLineRunner {

    private final AppUserService userService;
    private final AppUserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Create ADMIN user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            userService.registerUser("admin", "admin", "ROLE_ADMIN");
            System.out.println("ADMIN USER CREATED (Username: admin / Password: admin)");
        }

        // 2. Create CUSTOMER user for testing
        if (!userRepository.existsByUsername("user")) {
            userService.registerUser("user", "user", "ROLE_USER");
            System.out.println("TEST USER CREATED (Username: user / Password: user)");
        }
    }
}