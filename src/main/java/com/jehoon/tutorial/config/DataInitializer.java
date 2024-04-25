package com.jehoon.tutorial.config;

import com.jehoon.tutorial.entity.User;
import com.jehoon.tutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }
        initUser();
    }

    private void initUser() {
        var users = new ArrayList<User>();

        users.add(User.builder()
                .id("admin")
                .password(passwordEncoder.encode("P@ssw0rd"))
                .name("관리자")
                .build());

        users.add(User.builder()
                .id("user")
                .password(passwordEncoder.encode("P@ssw0rd"))
                .name("사용자")
                .build());

        userRepository.saveAll(users);
    }
}
