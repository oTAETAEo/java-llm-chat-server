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
        assertThat(workout.getTitle()).isEqualTo("자전거 42.5km");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.DIRECT_INPUT);
        assertThat(workout.getFeedbackCount()).isZero();
        assertThat(workout.getDistance()).isEqualTo(42.5);
        assertThat(workout.getAvgSpeed()).isEqualTo(27.4);
        assertThat(workout.getFtp()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("자전거 운동 피드백 횟수를 1 증가시킨다")
    void increaseCyclingWorkoutFeedbackCount() {
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .member(MemberFixture.builder().build())
                .build();

        workout.increaseFeedbackCount();

        assertThat(workout.getFeedbackCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("자전거 운동 제목과 입력 출처를 저장한다")
    void createCyclingWorkoutWithMetadata() {
        Member member = MemberFixture.builder().build();

        CyclingWorkout workout = CyclingWorkout.create(
                member,
                AthleteTier.PRO,
                "한강 템포 라이딩",
                WorkoutInputSource.FIT_FILE,
                CyclingWorkoutFixture.builder().commonCommand(),
                CyclingWorkoutFixture.builder().cyclingCommand()
        );

        assertThat(workout.getTitle()).isEqualTo("한강 템포 라이딩");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.FIT_FILE);
    }

    @Test
    @DisplayName("자전거 운동 제목과 입력 출처를 갱신한다")
    void updateCyclingWorkoutMetadata() {
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .title("이전 라이딩")
                .inputSource(WorkoutInputSource.DIRECT_INPUT)
                .build();
        CyclingWorkout updatedWorkout = CyclingWorkoutFixture.builder()
                .title("새 라이딩")
                .inputSource(WorkoutInputSource.FIT_FILE)
                .build();

        workout.updateMetadataFrom(updatedWorkout);

        assertThat(workout.getTitle()).isEqualTo("새 라이딩");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.FIT_FILE);
    }

    @Test
    @DisplayName("자전거 운동 제목이 공백이면 도메인 기본 제목을 사용한다")
    void createCyclingWorkoutWithBlankTitle() {
        Member member = MemberFixture.builder().build();

        CyclingWorkout workout = CyclingWorkout.create(
                member,
                AthleteTier.PRO,
                "  ",
                WorkoutInputSource.DIRECT_INPUT,
                CyclingWorkoutFixture.builder().commonCommand(),
                CyclingWorkoutFixture.builder().cyclingCommand()
        );

        assertThat(workout.getTitle()).isEqualTo("자전거 42.5km");
        assertThat(workout.getInputSource()).isEqualTo(WorkoutInputSource.DIRECT_INPUT);
    }

    @Test
    @DisplayName("자전거 운동 입력 출처가 없으면 생성에 실패한다")
    void createCyclingWorkoutWithoutInputSourceFails() {
        assertThatThrownBy(() -> CyclingWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.PRO,
                null,
                null,
                CyclingWorkoutFixture.builder().commonCommand(),
                CyclingWorkoutFixture.builder().cyclingCommand()
        )).isInstanceOf(IllegalArgumentException.class);
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
