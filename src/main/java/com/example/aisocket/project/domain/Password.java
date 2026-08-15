package com.example.aisocket.project.domain;

import com.example.aisocket.project.domain.security.PasswordHasher;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 64;
    public static final String STRONG_PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S+$";
    public static final String STRONG_PASSWORD_MESSAGE = "비밀번호는 8~64자이며 영문자, 숫자, 특수문자를 각각 1개 이상 포함하고 공백이 없어야 합니다.";

    @Column(name = "password")
    private String value;

    public static Password fromRaw(String rawPassword, PasswordHasher passwordHasher) {
        validatePasswordHasher(passwordHasher);
        validateRawPassword(rawPassword);
        return encoded(passwordHasher.hash(rawPassword));
    }

    public static Password encoded(String encodedPassword) {
        return new Password(encodedPassword);
    }

    private Password(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호(password)는 필수 값입니다.");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    private static void validatePasswordHasher(PasswordHasher passwordHasher) {
        if (passwordHasher == null) {
            throw new IllegalArgumentException("비밀번호 해시 정책(passwordHasher)은 필수 값입니다.");
        }
    }

    private static void validateRawPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호(rawPassword)는 필수 값입니다.");
        }
        if (rawPassword.length() < MIN_LENGTH || rawPassword.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(STRONG_PASSWORD_MESSAGE);
        }
        if (!rawPassword.matches(STRONG_PASSWORD_REGEX)) {
            throw new IllegalArgumentException(STRONG_PASSWORD_MESSAGE);
        }
    }
}
