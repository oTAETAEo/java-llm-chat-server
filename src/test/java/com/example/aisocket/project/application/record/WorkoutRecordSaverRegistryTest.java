package com.example.aisocket.project.application.record;

import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateCyclingWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class WorkoutRecordSaverRegistryTest {

    private final RunningWorkoutRepository runningWorkoutRepository = mock(RunningWorkoutRepository.class);
    private final CyclingWorkoutRepository cyclingWorkoutRepository = mock(CyclingWorkoutRepository.class);

    private final WorkoutRecordSaverRegistry registry = new WorkoutRecordSaverRegistry(List.of(
            new RunningWorkoutRecordSaver(runningWorkoutRepository),
            new CyclingWorkoutRecordSaver(cyclingWorkoutRepository)
    ));

    @Test
    @DisplayName("러닝 운동이면 러닝 저장 전략을 선택한다")
    void saveRunningWorkout() {
        Member member = Member.of(1L, "runner");
        RunningWorkout workout = runningWorkout();
        when(runningWorkoutRepository.save(member, workout, AthleteTier.AMATEUR)).thenReturn(10L);

        Long workoutId = registry.save(member, workout, AthleteTier.AMATEUR);

        assertThat(workoutId).isEqualTo(10L);
        verify(runningWorkoutRepository).save(member, workout, AthleteTier.AMATEUR);
        verifyNoInteractions(cyclingWorkoutRepository);
    }

    @Test
    @DisplayName("자전거 운동이면 자전거 저장 전략을 선택한다")
    void saveCyclingWorkout() {
        Member member = Member.of(1L, "rider");
        CyclingWorkout workout = cyclingWorkout();
        when(cyclingWorkoutRepository.save(member, workout, AthleteTier.PRO)).thenReturn(20L);

        Long workoutId = registry.save(member, workout, AthleteTier.PRO);

        assertThat(workoutId).isEqualTo(20L);
        verify(cyclingWorkoutRepository).save(member, workout, AthleteTier.PRO);
        verifyNoInteractions(runningWorkoutRepository);
    }

    @Test
    @DisplayName("지원하지 않는 운동 타입이면 저장 전략 선택에 실패한다")
    void saveUnsupportedWorkoutFails() {
        assertThatThrownBy(() -> registry.save(Member.of(1L, "user"), unsupportedWorkout(), AthleteTier.AMATEUR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 운동 타입");

        verifyNoInteractions(runningWorkoutRepository, cyclingWorkoutRepository);
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
