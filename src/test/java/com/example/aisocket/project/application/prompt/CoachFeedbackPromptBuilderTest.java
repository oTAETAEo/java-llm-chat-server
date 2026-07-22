package com.example.aisocket.project.application.prompt;

import com.example.aisocket.project.application.prompt.workout.CyclingWorkoutPromptSectionBuilder;
import com.example.aisocket.project.application.prompt.workout.RunningWorkoutPromptSectionBuilder;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateCyclingWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import com.example.aisocket.project.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoachFeedbackPromptBuilderTest {

    private final CoachFeedbackPromptBuilder promptBuilder = new CoachFeedbackPromptBuilder(List.of(
            new RunningWorkoutPromptSectionBuilder(),
            new CyclingWorkoutPromptSectionBuilder()
    ));

    @Test
    @DisplayName("러닝 운동이면 공통 프롬프트와 러닝 전용 프롬프트를 생성한다")
    void buildRunningWorkoutPrompt() {
        String prompt = promptBuilder.build(runningWorkout(), AthleteTier.AMATEUR);

        assertThat(prompt)
                .contains("현재 피드백을 받는 사용자의 등급 수준은 [AMATEUR]")
                .contains("[기본 운동 통계 정보]")
                .contains("- 운동 거리: 8.20 km")
                .contains("[러닝 전용 분석 지표]")
                .contains("- 평균 페이스: 5.48 min/km / 최고 페이스: 4.92 min/km")
                .contains("- 총 걸음 수: 7600 걸음");
    }

    @Test
    @DisplayName("자전거 운동이면 공통 프롬프트와 자전거 전용 프롬프트를 생성한다")
    void buildCyclingWorkoutPrompt() {
        String prompt = promptBuilder.build(cyclingWorkout(), AthleteTier.PRO);

        assertThat(prompt)
                .contains("현재 피드백을 받는 사용자의 등급 수준은 [PRO]")
                .contains("[기본 운동 통계 정보]")
                .contains("- 운동 거리: 42.50 km")
                .contains("[자전거 전용 분석 지표]")
                .contains("- 평균 속도: 27.4 km/h / 최고 속도: 44.1 km/h")
                .contains("- 현재 유저 FTP: 250.0 W");
    }

    @Test
    @DisplayName("지원하지 않는 운동 타입이면 프롬프트 생성에 실패한다")
    void buildUnsupportedWorkoutPromptFails() {
        assertThatThrownBy(() -> promptBuilder.build(unsupportedWorkout(), AthleteTier.AMATEUR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 운동 타입");
    }

    @Test
    @DisplayName("운동 등급이 없으면 프롬프트 생성에 실패한다")
    void buildPromptWithoutTierFails() {
        assertThatThrownBy(() -> promptBuilder.build(runningWorkout(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 등급");
    }

    private RunningWorkout runningWorkout() {
        return RunningWorkout.create(
                member(),
                AthleteTier.AMATEUR,
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
        return CyclingWorkout.create(
                member(),
                AthleteTier.PRO,
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

    private Member member() {
        return Member.of(1L, null, null, "test-member");
    }

}
