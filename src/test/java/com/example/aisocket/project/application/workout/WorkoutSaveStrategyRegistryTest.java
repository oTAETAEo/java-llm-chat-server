package com.example.aisocket.project.application.workout;

import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
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

class WorkoutSaveStrategyRegistryTest {

    private final RunningWorkoutRepository runningWorkoutRepository = mock(RunningWorkoutRepository.class);
    private final CyclingWorkoutRepository cyclingWorkoutRepository = mock(CyclingWorkoutRepository.class);

    private final WorkoutSaveStrategyRegistry registry = new WorkoutSaveStrategyRegistry(List.of(
            new RunningWorkoutSaveStrategy(runningWorkoutRepository),
            new CyclingWorkoutSaveStrategy(cyclingWorkoutRepository)
    ));

    @Test
    @DisplayName("러닝 운동이면 러닝 저장 전략을 선택한다")
    void saveRunningWorkout() {
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
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
        Member member = MemberFixture.builder().id(1L).nickname("rider").build();
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
        assertThatThrownBy(() -> registry.save(MemberFixture.builder().id(1L).nickname("user").build(), unsupportedWorkout(), AthleteTier.AMATEUR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 운동 타입");

        verifyNoInteractions(runningWorkoutRepository, cyclingWorkoutRepository);
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
