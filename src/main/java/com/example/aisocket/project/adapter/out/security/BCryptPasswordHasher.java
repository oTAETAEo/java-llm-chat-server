package com.example.aisocket.project.adapter.out.security;

import com.example.aisocket.project.domain.security.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        validateRequired(rawPassword, "비밀번호(rawPassword)");
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        validateRequired(rawPassword, "비밀번호(rawPassword)");
        validateRequired(hashedPassword, "해시 비밀번호(hashedPassword)");
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "는 필수 값입니다.");
        }
    }
}
