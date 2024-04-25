package com.jehoon.tutorial.service;

import com.jehoon.tutorial.dto.LoginRequest;
import com.jehoon.tutorial.entity.User;
import com.jehoon.tutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User select(LoginRequest param) {
        var user = userRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다"));

        if (!passwordEncoder.matches(param.getPassword(), user.getPassword())) {
            throw new RuntimeException("패스워드가 일치하지 않습니다");
        }
        return user;
    }
}
