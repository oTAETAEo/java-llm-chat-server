package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackRoomWorkoutTest {

    @Test
    @DisplayName("피드백 방과 운동 기록을 연결한다")
    void createFeedbackRoomWorkout() {
        FeedbackRoom room = FeedbackRoomFixture.builder().build();

        FeedbackRoomWorkout roomWorkout = FeedbackRoomWorkoutFixture.builder()
                .room(room)
                .workoutType(WorkOutType.CYCLING)
                .workoutId(20L)
                .build();

        assertThat(roomWorkout.getId()).isNull();
        assertThat(roomWorkout.getRoom()).isSameAs(room);
        assertThat(roomWorkout.getWorkoutType()).isEqualTo(WorkOutType.CYCLING);
        assertThat(roomWorkout.getWorkoutId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("방이 없으면 피드백 방 운동 연결 생성에 실패한다")
    void createWithoutRoomFails() {
        assertThatThrownBy(() -> FeedbackRoomWorkoutFixture.builder()
                .room(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 타입이 없으면 피드백 방 운동 연결 생성에 실패한다")
    void createWithoutWorkoutTypeFails() {
        assertThatThrownBy(() -> FeedbackRoomWorkoutFixture.builder()
                .workoutType(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 ID가 없으면 피드백 방 운동 연결 생성에 실패한다")
    void createWithoutWorkoutIdFails() {
        assertThatThrownBy(() -> FeedbackRoomWorkoutFixture.builder()
                .workoutId(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
