package com.example.aisocket.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "feedback_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackRoom extends BaseEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean pinned;

    public static FeedbackRoom create(Member member, String title) {
        return new FeedbackRoom(UUID.randomUUID(), member, title);
    }

    private FeedbackRoom(UUID id, Member member, String title) {
        this.id = id;
        this.member = member;
        this.title = normalizeTitle(title);
        this.pinned = false;
        validate();
    }

    public boolean isOwnedBy(Long memberId) {
        return member != null && member.getId() != null && member.getId().equals(memberId);
    }

    public void rename(String title) {
        this.title = normalizeTitle(title);
        validate();
    }

    public void pin() {
        this.pinned = true;
    }

    public void unpin() {
        this.pinned = false;
    }

    private String normalizeTitle(String title) {
        if (title == null || title.isBlank()) {
            return "새 운동 피드백";
        }
        String trimmed = title.trim();
        return trimmed.length() > 80 ? trimmed.substring(0, 80) : trimmed;
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("피드백 방 ID(id)는 필수 값입니다.");
        }
        if (member == null || member.getId() == null) {
            throw new IllegalArgumentException("피드백 방 회원(member)은 필수 값입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("피드백 방 제목(title)은 필수 값입니다.");
        }
    }
}
