package com.example.aisocket.project.application.internal.prompt;

import com.example.aisocket.project.application.internal.prompt.embedding.CyclingWorkoutEmbeddingPromptStrategy;
import com.example.aisocket.project.application.internal.prompt.embedding.RunningWorkoutEmbeddingPromptStrategy;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import com.example.aisocket.project.domain.MemberFixture;
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
                .isInstanceOf(IllegalArgumentException.class);
    }

    private RunningWorkout runningWorkout() {
        return RunningWorkoutFixture.builder()
                .member(MemberFixture.builder().build())
                .tier(AthleteTier.AMATEUR)
                .build();
    }

    private CyclingWorkout cyclingWorkout() {
        return CyclingWorkoutFixture.builder()
                .member(MemberFixture.builder().build())
                .tier(AthleteTier.PRO)
                .build();
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
