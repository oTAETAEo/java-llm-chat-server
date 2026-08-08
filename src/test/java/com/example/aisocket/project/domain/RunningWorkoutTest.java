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
        assertThat(workout.getTitle()).isEqualTo("러닝 8.2km");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.DIRECT_INPUT);
        assertThat(workout.getFeedbackCount()).isZero();
        assertThat(workout.getDistance()).isEqualTo(8.2);
        assertThat(workout.getAvgPace()).isEqualTo(5.48);
        assertThat(workout.getSteps()).isEqualTo(7600);
    }

    @Test
    @DisplayName("러닝 운동 피드백 횟수를 1 증가시킨다")
    void increaseRunningWorkoutFeedbackCount() {
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(MemberFixture.builder().build())
                .build();

        workout.increaseFeedbackCount();

        assertThat(workout.getFeedbackCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("러닝 운동 제목과 입력 출처를 저장한다")
    void createRunningWorkoutWithMetadata() {
        Member member = MemberFixture.builder().build();

        RunningWorkout workout = RunningWorkout.create(
                member,
                AthleteTier.AMATEUR,
                "남산 순환 러닝",
                WorkoutInputSource.FIT_FILE,
                RunningWorkoutFixture.builder().commonCommand(),
                RunningWorkoutFixture.builder().runningCommand()
        );

        assertThat(workout.getTitle()).isEqualTo("남산 순환 러닝");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.FIT_FILE);
    }

    @Test
    @DisplayName("러닝 운동 제목과 입력 출처를 갱신한다")
    void updateRunningWorkoutMetadata() {
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .title("이전 러닝")
                .inputSource(WorkoutInputSource.DIRECT_INPUT)
                .build();
        RunningWorkout updatedWorkout = RunningWorkoutFixture.builder()
                .title("새 러닝")
                .inputSource(WorkoutInputSource.FIT_FILE)
                .build();

        workout.updateMetadataFrom(updatedWorkout);

        assertThat(workout.getTitle()).isEqualTo("새 러닝");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.FIT_FILE);
    }

    @Test
    @DisplayName("러닝 운동 제목이 공백이면 도메인 기본 제목을 사용한다")
    void createRunningWorkoutWithBlankTitle() {
        Member member = MemberFixture.builder().build();

        RunningWorkout workout = RunningWorkout.create(
                member,
                AthleteTier.AMATEUR,
                "  ",
                WorkoutInputSource.DIRECT_INPUT,
                RunningWorkoutFixture.builder().commonCommand(),
                RunningWorkoutFixture.builder().runningCommand()
        );

        assertThat(workout.getTitle()).isEqualTo("러닝 8.2km");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.DIRECT_INPUT);
    }

    @Test
    @DisplayName("러닝 운동 입력 출처가 없으면 생성에 실패한다")
    void createRunningWorkoutWithoutInputSourceFails() {
        assertThatThrownBy(() -> RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                null,
                null,
                RunningWorkoutFixture.builder().commonCommand(),
                RunningWorkoutFixture.builder().runningCommand()
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 시작 시간이 없으면 러닝 운동 생성에 실패한다")
    void createWithoutStartedAtFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .startedAt(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 종료 시간이 없으면 러닝 운동 생성에 실패한다")
    void createWithoutEndedAtFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .endedAt(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 종료 시간이 시작 시간보다 빠르면 러닝 운동 생성에 실패한다")
    void createWithEndedAtBeforeStartedAtFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .startedAt(LocalDateTime.of(2026, 7, 18, 7, 45))
                .endedAt(LocalDateTime.of(2026, 7, 18, 7, 0))
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 시간이 0 이하이면 러닝 운동 생성에 실패한다")
    void createWithNonPositiveMovingTimeFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .movingTime(0)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("운동 거리가 음수이면 러닝 운동 생성에 실패한다")
    void createWithNegativeDistanceFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .distance(-1.0)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("걸음 수가 음수이면 러닝 운동 생성에 실패한다")
    void createWithNegativeStepsFails() {
        assertThatThrownBy(() -> RunningWorkoutFixture.builder()
                .steps(-1)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
