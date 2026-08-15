package com.example.aisocket.project.domain;

import com.example.aisocket.project.domain.security.PasswordHasher;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Embedded
    private Password password;

    @Column(nullable = false)
    private String nickname;

    public static Member create(String email, String rawPassword, String nickname, PasswordHasher passwordHasher) {
        return new Member(email, Password.fromRaw(rawPassword, passwordHasher), nickname);
    }

    public static Member of(Long id, String email, String password, String nickname) {
        Member member = new Member(email, Password.encoded(password), nickname);
        member.id = id;
        return member;
    }

    private Member(String email, Password password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;

        validate();
    }

    public String getPassword() {
        return password.value();
    }

    private void validate() {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임(nickname)은 필수 값입니다.");
        }
        validateLoginFields();
    }

    private void validateLoginFields() {
        if (email == null && password == null) {
            return;
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일(email)은 필수 값입니다.");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("이메일(email) 형식이 올바르지 않습니다.");
        }
        if (password == null) {
            throw new IllegalArgumentException("비밀번호(password)는 필수 값입니다.");
        }
    }
}
