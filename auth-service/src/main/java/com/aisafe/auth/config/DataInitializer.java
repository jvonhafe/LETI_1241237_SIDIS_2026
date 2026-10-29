package com.aisafe.auth.config;

import com.aisafe.auth.model.Role;
import com.aisafe.auth.model.User;
import com.aisafe.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** WP0 (setup): cria os utilizadores iniciais, com passwords em BCrypt (nunca em claro no import.sql). */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder,
                                @Value("${aisafe.bootstrap.admin-password}") String adminPassword,
                                @Value("${aisafe.bootstrap.default-password}") String defaultPassword) {
        return args -> {
            seed(users, encoder, "admin", adminPassword, Role.ADMIN);
            seed(users, encoder, "backoffice", defaultPassword, Role.BACKOFFICE);
            seed(users, encoder, "atcc_user", defaultPassword, Role.ATCC);
            seed(users, encoder, "tech", defaultPassword, Role.MAINTENANCE_TECH);
            seed(users, encoder, "supervisor", defaultPassword, Role.MAINTENANCE_SUPERVISOR);
        };
    }

    private void seed(UserRepository users, PasswordEncoder encoder, String username, String rawPassword, Role role) {
        if (!users.existsByUsername(username)) {
            users.save(new User(username, encoder.encode(rawPassword), role));
        }
    }
}
