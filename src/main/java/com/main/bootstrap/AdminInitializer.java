package com.main.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.main.entities.UserEntity;
import com.main.enums.Role;
import com.main.repositories.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value
    ("${admin.bootstrap.enabled:false}")
    private boolean bootstrapEnabled;

    @Value
    ("${admin.email:noorsonu11@gmail.com}")
    private String adminEmail;

    @Value
    ("${admin.password:Noormd870@}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            if (!bootstrapEnabled) {
                return;
            }
            boolean adminExists = userRepository.existsByRole(Role.ADMIN);
            if (!adminExists) {
                UserEntity admin = new UserEntity();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("[AdminInitializer] Seeded default admin '" + adminEmail + "'. Change credentials in application.properties.");
            }
        } catch (Exception e) {
            System.err.println("[AdminInitializer] Failed to seed admin: " + e.getMessage());
        }
    }
}
