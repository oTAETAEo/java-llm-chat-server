package com.example.aisocket.project.domain.security;

public class TestPasswordHasher implements PasswordHasher {

    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호(rawPassword)는 필수 값입니다.");
        }
        return "hashed-password:" + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return hash(rawPassword).equals(hashedPassword);
    }
}
