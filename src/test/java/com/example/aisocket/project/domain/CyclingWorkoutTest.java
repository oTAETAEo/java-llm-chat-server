package com.example.aisocket.project.domain;

import com.example.aisocket.project.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CyclingWorkoutTest {

    @Test
    @DisplayName("자전거 운동을 생성한다")
    void createCyclingWorkout() {
        CyclingWorkout workout = CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 9, 0),
                        LocalDateTime.of(2026, 7, 18, 10, 30),
                        42.5,
                        90
                ),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        );

        assertThat(workout.getWorkOutType()).isEqualTo(WorkOutType.CYCLING);
        assertThat(workout.getDistance()).isEqualTo(42.5);
        assertThat(workout.getAvgSpeed()).isEqualTo(27.4);
        assertThat(workout.getFtp()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("운동 시작 시간이 없으면 자전거 운동 생성에 실패한다")
    void createWithoutStartedAtFails() {
        assertThatThrownBy(() -> CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                commonCommand(null, LocalDateTime.of(2026, 7, 18, 10, 30), 42.5, 90),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 시작 시간");
    }

    @Test
    @DisplayName("운동 종료 시간이 없으면 자전거 운동 생성에 실패한다")
    void createWithoutEndedAtFails() {
        assertThatThrownBy(() -> CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                commonCommand(LocalDateTime.of(2026, 7, 18, 9, 0), null, 42.5, 90),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 종료 시간");
    }

    @Test
    @DisplayName("운동 종료 시간이 시작 시간보다 빠르면 자전거 운동 생성에 실패한다")
    void createWithEndedAtBeforeStartedAtFails() {
        assertThatThrownBy(() -> CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 10, 30),
                        LocalDateTime.of(2026, 7, 18, 9, 0),
                        42.5,
                        90
                ),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간");
    }

    @Test
    @DisplayName("운동 시간이 0 이하이면 자전거 운동 생성에 실패한다")
    void createWithNonPositiveMovingTimeFails() {
        assertThatThrownBy(() -> CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 9, 0),
                        LocalDateTime.of(2026, 7, 18, 10, 30),
                        42.5,
                        0
                ),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 시간");
    }

    @Test
    @DisplayName("운동 거리가 음수이면 자전거 운동 생성에 실패한다")
    void createWithNegativeDistanceFails() {
        assertThatThrownBy(() -> CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 9, 0),
                        LocalDateTime.of(2026, 7, 18, 10, 30),
                        -1.0,
                        90
                ),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 거리");
    }

    @Test
    @DisplayName("FTP가 음수이면 자전거 운동 생성에 실패한다")
    void createWithNegativeFtpFails() {
        assertThatThrownBy(() -> CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 9, 0),
                        LocalDateTime.of(2026, 7, 18, 10, 30),
                        42.5,
                        90
                ),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, -1.0)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FTP");
    }

    private CreateCommonWorkoutCommand commonCommand(
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            Double distance,
            Integer movingTime
    ) {
        return new CreateCommonWorkoutCommand(
                startedAt,
                endedAt,
                distance,
                650.0,
                240.0,
                movingTime,
                920.0,
                88.0,
                104.0,
                168.0,
                142.0
        );
    }


}
