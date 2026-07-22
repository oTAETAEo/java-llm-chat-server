package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CyclingWorkoutTest {

    @Test
    @DisplayName("자전거 운동을 생성한다")
    void createCyclingWorkout() {
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .member(MemberFixture.builder().build())
                .tier(AthleteTier.PRO)
                .startedAt(LocalDateTime.of(2026, 7, 18, 9, 0))
                .endedAt(LocalDateTime.of(2026, 7, 18, 10, 30))
                .distance(42.5)
                .movingTime(90)
                .avgSpeed(27.4)
                .maxSpeed(44.1)
                .avgPower(185.0)
                .maxPower(420.0)
                .ftp(250.0)
                .build();

        assertThat(workout.getWorkOutType()).isEqualTo(WorkOutType.CYCLING);
        assertThat(workout.getDistance()).isEqualTo(42.5);
        assertThat(workout.getAvgSpeed()).isEqualTo(27.4);
        assertThat(workout.getFtp()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("운동 시작 시간이 없으면 자전거 운동 생성에 실패한다")
    void createWithoutStartedAtFails() {
        assertThatThrownBy(() -> CyclingWorkoutFixture.builder()
                .startedAt(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 종료 시간이 없으면 자전거 운동 생성에 실패한다")
    void createWithoutEndedAtFails() {
        assertThatThrownBy(() -> CyclingWorkoutFixture.builder()
                .endedAt(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 종료 시간이 시작 시간보다 빠르면 자전거 운동 생성에 실패한다")
    void createWithEndedAtBeforeStartedAtFails() {
        assertThatThrownBy(() -> CyclingWorkoutFixture.builder()
                .startedAt(LocalDateTime.of(2026, 7, 18, 10, 30))
                .endedAt(LocalDateTime.of(2026, 7, 18, 9, 0))
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 시간이 0 이하이면 자전거 운동 생성에 실패한다")
    void createWithNonPositiveMovingTimeFails() {
        assertThatThrownBy(() -> CyclingWorkoutFixture.builder()
                .movingTime(0)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 거리가 음수이면 자전거 운동 생성에 실패한다")
    void createWithNegativeDistanceFails() {
        assertThatThrownBy(() -> CyclingWorkoutFixture.builder()
                .distance(-1.0)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("FTP가 음수이면 자전거 운동 생성에 실패한다")
    void createWithNegativeFtpFails() {
        assertThatThrownBy(() -> CyclingWorkoutFixture.builder()
                .ftp(-1.0)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
