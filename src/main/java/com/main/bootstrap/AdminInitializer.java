package com.main.bootstrap;

import com.main.entities.UserEntity;
import com.main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.phoneNumber}")
    private String adminPhoneNumber;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.countByUserType("ADMIN") == 0) {
            UserEntity admin = new UserEntity();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setUserType("ADMIN");
            admin.setPhoneNumber(adminPhoneNumber);
            userRepository.save(admin);
        }
    }
}