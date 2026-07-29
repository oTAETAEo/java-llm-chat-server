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
@Table(name = "feedback_room_workout")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackRoomWorkout extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private FeedbackRoom room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOutType workoutType;

    @Column(nullable = false)
    private Long workoutId;

    public static FeedbackRoomWorkout create(FeedbackRoom room, WorkOutType workoutType, Long workoutId) {
        return new FeedbackRoomWorkout(room, workoutType, workoutId);
    }

    private FeedbackRoomWorkout(FeedbackRoom room, WorkOutType workoutType, Long workoutId) {
        this.room = room;
        this.workoutType = workoutType;
        this.workoutId = workoutId;
        validate();
    }

    private void validate() {
        if (room == null || room.getId() == null) {
            throw new IllegalArgumentException("피드백 방 운동 연결의 방(room)은 필수 값입니다.");
        }
        if (workoutType == null) {
            throw new IllegalArgumentException("피드백 방 운동 연결의 운동 타입(workoutType)은 필수 값입니다.");
        }
        if (workoutId == null) {
            throw new IllegalArgumentException("피드백 방 운동 연결의 운동 ID(workoutId)는 필수 값입니다.");
        }
    }
}
