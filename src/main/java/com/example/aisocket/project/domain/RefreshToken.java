package com.example.aisocket.project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String token;

    private LocalDateTime expiresAt;

    private boolean revoked;

    public static RefreshToken create(Member member, String token, LocalDateTime expiresAt) {
        return new RefreshToken(member, token, expiresAt, false, LocalDateTime.now());
    }

    private RefreshToken(
            Member member,
            String token,
            LocalDateTime expiresAt,
            boolean revoked,
            LocalDateTime createdAt
    ) {
        this.member = member;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        initializeCreatedAt(createdAt);

        validate();
    }

    public void revoke() {
        this.revoked = true;
    }

    public Long getMemberId() {
        return member.getId();
    }

    public boolean isExpired(LocalDateTime now) {
        if (now == null) {
            throw new IllegalArgumentException("현재 시간(now)은 필수 값입니다.");
        }
        return !expiresAt.isAfter(now);
    }

    public boolean isUsable(LocalDateTime now) {
        return !revoked && !isExpired(now);
    }

    private void validate() {
        if (member == null) {
            throw new IllegalArgumentException("회원(member)은 필수 값입니다.");
        }
        if (member.getId() == null) {
            throw new IllegalArgumentException("회원 ID(member.id)는 리프레시 토큰에 필수 값입니다.");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰(token)은 필수 값입니다.");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("만료 시간(expiresAt)은 필수 값입니다.");
        }
        if (getCreatedAt() == null) {
            throw new IllegalArgumentException("생성 시간(createdAt)은 필수 값입니다.");
        }
    }
}
