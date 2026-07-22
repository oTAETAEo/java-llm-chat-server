package com.example.aisocket.project.domain;

import com.example.aisocket.project.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RunningWorkoutTest {

    @Test
    @DisplayName("러닝 운동을 생성한다")
    void createRunningWorkout() {
        RunningWorkout workout = RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        8.2,
                        45
                ),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        );

        assertThat(workout.getWorkOutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(workout.getDistance()).isEqualTo(8.2);
        assertThat(workout.getAvgPace()).isEqualTo(5.48);
        assertThat(workout.getSteps()).isEqualTo(7600);
    }

    @Test
    @DisplayName("운동 시작 시간이 없으면 러닝 운동 생성에 실패한다")
    void createWithoutStartedAtFails() {
        assertThatThrownBy(() -> RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                commonCommand(null, LocalDateTime.of(2026, 7, 18, 7, 45), 8.2, 45),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 시작 시간");
    }

    @Test
    @DisplayName("운동 종료 시간이 없으면 러닝 운동 생성에 실패한다")
    void createWithoutEndedAtFails() {
        assertThatThrownBy(() -> RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                commonCommand(LocalDateTime.of(2026, 7, 18, 7, 0), null, 8.2, 45),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 종료 시간");
    }

    @Test
    @DisplayName("운동 종료 시간이 시작 시간보다 빠르면 러닝 운동 생성에 실패한다")
    void createWithEndedAtBeforeStartedAtFails() {
        assertThatThrownBy(() -> RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        8.2,
                        45
                ),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간");
    }

    @Test
    @DisplayName("운동 시간이 0 이하이면 러닝 운동 생성에 실패한다")
    void createWithNonPositiveMovingTimeFails() {
        assertThatThrownBy(() -> RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        8.2,
                        0
                ),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 시간");
    }

    @Test
    @DisplayName("운동 거리가 음수이면 러닝 운동 생성에 실패한다")
    void createWithNegativeDistanceFails() {
        assertThatThrownBy(() -> RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        -1.0,
                        45
                ),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 거리");
    }

    @Test
    @DisplayName("걸음 수가 음수이면 러닝 운동 생성에 실패한다")
    void createWithNegativeStepsFails() {
        assertThatThrownBy(() -> RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                commonCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        8.2,
                        45
                ),
                new CreateRunningWorkoutCommand(5.48, 4.92, -1)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("걸음 수");
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
                120.0,
                85.0,
                movingTime,
                530.0,
                172.0,
                188.0,
                176.0,
                148.0
        );
    }


}
