package com.cts.config;

import com.cts.entity.InternalUser;
import com.cts.enums.Role;
import com.cts.repository.InternalUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final InternalUserRepository internalUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        internalUserRepository.findByEmail("admin@promoengine.com").ifPresentOrElse(
                user -> log.info("Admin user already present"),
                () -> {
                    InternalUser admin = new InternalUser();
                    admin.setName("Retail Admin");
                    admin.setEmail("admin@promoengine.com");
                    admin.setPasswordHash(passwordEncoder.encode("admin@123"));
                    admin.setRole(Role.ADMIN);
                    admin.setPhone("9999999999");
                    internalUserRepository.save(admin);
                    log.info("Seeded default admin user");
                });
    }
}