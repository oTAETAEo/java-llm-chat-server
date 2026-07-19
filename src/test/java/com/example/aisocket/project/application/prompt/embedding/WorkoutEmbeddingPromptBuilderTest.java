package com.example.aisocket.project.application.prompt.embedding;

import com.example.aisocket.project.application.prompt.WorkoutEmbeddingPromptBuilder;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateCyclingWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkoutEmbeddingPromptBuilderTest {

    private final WorkoutEmbeddingPromptBuilder promptBuilder = new WorkoutEmbeddingPromptBuilder(List.of(
            new RunningWorkoutEmbeddingPromptStrategy(),
            new CyclingWorkoutEmbeddingPromptStrategy()
    ));

    @Test
    @DisplayName("러닝 운동에 맞는 임베딩 원문 전략을 선택한다")
    void buildRunningWorkoutContent() {
        String content = promptBuilder.build(runningWorkout(), AthleteTier.AMATEUR);

        assertThat(content)
                .contains("[러닝 운동 기록]")
                .contains("사용자 등급: AMATEUR")
                .contains("운동 타입: RUNNING")
                .contains("거리: 8.20 km")
                .contains("평균 페이스: 5.48 min/km")
                .contains("걸음 수: 7600");
    }

    @Test
    @DisplayName("자전거 운동에 맞는 임베딩 원문 전략을 선택한다")
    void buildCyclingWorkoutContent() {
        String content = promptBuilder.build(cyclingWorkout(), AthleteTier.PRO);

        assertThat(content)
                .contains("[자전거 운동 기록]")
                .contains("사용자 등급: PRO")
                .contains("운동 타입: CYCLING")
                .contains("거리: 42.50 km")
                .contains("평균 속도: 27.4 km/h")
                .contains("FTP: 250.0 W");
    }

    @Test
    @DisplayName("지원하지 않는 운동 타입이면 실패한다")
    void buildUnsupportedWorkoutFails() {
        Workout unsupportedWorkout = unsupportedWorkout();

        assertThatThrownBy(() -> promptBuilder.build(unsupportedWorkout, AthleteTier.AMATEUR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 운동 타입");
    }

    private RunningWorkout runningWorkout() {
        return RunningWorkout.of(
                commonWorkoutCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        8.2,
                        45
                ),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        );
    }

    private CyclingWorkout cyclingWorkout() {
        return CyclingWorkout.of(
                commonWorkoutCommand(
                        LocalDateTime.of(2026, 7, 18, 9, 0),
                        LocalDateTime.of(2026, 7, 18, 10, 30),
                        42.5,
                        90
                ),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        );
    }

    private CreateCommonWorkoutCommand commonWorkoutCommand(
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

    private Workout unsupportedWorkout() {
        return new Workout() {
            @Override public LocalDateTime getStartedAt() { return LocalDateTime.of(2026, 7, 18, 7, 0); }
            @Override public LocalDateTime getEndedAt() { return LocalDateTime.of(2026, 7, 18, 7, 45); }
            @Override public Double getDistance() { return 1.0; }
            @Override public Double getElevGain() { return 1.0; }
            @Override public Double getElevationMax() { return 1.0; }
            @Override public Integer getMovingTime() { return 1; }
            @Override public Double getCalories() { return 1.0; }
            @Override public Double getAvgCadence() { return 1.0; }
            @Override public Double getMaxCadence() { return 1.0; }
            @Override public Double getMaxHeartRate() { return 1.0; }
            @Override public Double getAvgHeartRate() { return 1.0; }
            @Override public void validate() { }
            @Override public WorkOutType getWorkOutType() { return null; }
        };
    }
}
