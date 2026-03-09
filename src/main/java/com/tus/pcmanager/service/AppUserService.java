package com.tus.pcmanager.service;

import com.tus.pcmanager.exception.UserAlreadyExistsException;
import com.tus.pcmanager.model.AppUser;
import com.tus.pcmanager.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser registerUser(String username, String password, String role) {
        
        if (Boolean.TRUE.equals(userRepository.existsByUsername(username))) {
            throw new UserAlreadyExistsException("Username '" + username + "' is already taken.");
        }
        
        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);
        
        return userRepository.save(newUser);
    }
}