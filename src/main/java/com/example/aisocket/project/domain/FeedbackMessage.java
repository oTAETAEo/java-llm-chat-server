package com.example.aisocket.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Entity
@Table(name = "feedback_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private FeedbackRoom room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackMessageRole role;

    @Enumerated(EnumType.STRING)
    private WorkOutType workoutType;

    private Long workoutId;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    public static FeedbackMessage createUserWorkoutMessage(FeedbackRoom room, WorkOutType workoutType, Long workoutId, String content) {
        return new FeedbackMessage(room, FeedbackMessageRole.USER, workoutType, workoutId, content);
    }

    public static FeedbackMessage createAssistantMessage(FeedbackRoom room, WorkOutType workoutType, Long workoutId, String content) {
        return new FeedbackMessage(room, FeedbackMessageRole.ASSISTANT, workoutType, workoutId, content);
    }

    private FeedbackMessage(FeedbackRoom room, FeedbackMessageRole role, WorkOutType workoutType, Long workoutId, String content) {
        this.room = room;
        this.role = role;
        this.workoutType = workoutType;
        this.workoutId = workoutId;
        this.content = content;
        validate();
    }

    private void validate() {
        if (room == null || room.getId() == null) {
            throw new IllegalArgumentException("피드백 메시지 방(room)은 필수 값입니다.");
        }
        if (role == null) {
            throw new IllegalArgumentException("피드백 메시지 역할(role)은 필수 값입니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("피드백 메시지 내용(content)은 필수 값입니다.");
        }
    }
}
