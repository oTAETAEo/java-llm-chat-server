package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RunningWorkoutTest {

    @Test
    @DisplayName("러닝 운동을 생성한다")
    void createRunningWorkout() {
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(MemberFixture.builder().build())
                .tier(AthleteTier.AMATEUR)
                .startedAt(LocalDateTime.of(2026, 7, 18, 7, 0))
                .endedAt(LocalDateTime.of(2026, 7, 18, 7, 45))
                .distance(8.2)
                .movingTime(45)
                .avgPace(5.48)
                .maxPace(4.92)
                .steps(7600)
                .build();

        assertThat(workout.getWorkOutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(workout.getDistance()).isEqualTo(8.2);
        assertThat(workout.getAvgPace()).isEqualTo(5.48);
        assertThat(workout.getSteps()).isEqualTo(7600);
    }

    @Test
    @DisplayName("운동 시작 시간이 없으면 러닝 운동 생성에 실패한다")
    void createWithoutStartedAtFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .startedAt(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 시작 시간");
    }

    @Test
    @DisplayName("운동 종료 시간이 없으면 러닝 운동 생성에 실패한다")
    void createWithoutEndedAtFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .endedAt(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 종료 시간");
    }

    @Test
    @DisplayName("운동 종료 시간이 시작 시간보다 빠르면 러닝 운동 생성에 실패한다")
    void createWithEndedAtBeforeStartedAtFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .startedAt(LocalDateTime.of(2026, 7, 18, 7, 45))
                .endedAt(LocalDateTime.of(2026, 7, 18, 7, 0))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간");
    }

    @Test
    @DisplayName("운동 시간이 0 이하이면 러닝 운동 생성에 실패한다")
    void createWithNonPositiveMovingTimeFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .movingTime(0)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 시간");
    }

    @Test
    @DisplayName("운동 거리가 음수이면 러닝 운동 생성에 실패한다")
    void createWithNegativeDistanceFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .distance(-1.0)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 거리");
    }

    @Test
    @DisplayName("걸음 수가 음수이면 러닝 운동 생성에 실패한다")
    void createWithNegativeStepsFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .steps(-1)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("걸음 수");
    }
}
