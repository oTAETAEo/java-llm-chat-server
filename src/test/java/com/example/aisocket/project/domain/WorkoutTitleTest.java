package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkoutTitleTest {

    @Test
    @DisplayName("운동 타입과 거리로 기본 제목을 만든다")
    void defaultTitleWithDistance() {
        assertThat(WorkoutTitle.defaultTitle(WorkOutType.RUNNING, 8.2)).isEqualTo("러닝 8.2km");
        assertThat(WorkoutTitle.defaultTitle(WorkOutType.CYCLING, 42.5)).isEqualTo("자전거 42.5km");
    }

    @Test
    @DisplayName("거리가 없으면 운동 타입 이름으로 기본 제목을 만든다")
    void defaultTitleWithoutDistance() {
        assertThat(WorkoutTitle.defaultTitle(WorkOutType.RUNNING, null)).isEqualTo("러닝 운동");
        assertThat(WorkoutTitle.defaultTitle(WorkOutType.CYCLING, null)).isEqualTo("자전거 운동");
    }

    @Test
    @DisplayName("입력 제목이 있으면 공백을 제거해서 사용한다")
    void normalizeWithTitle() {
        assertThat(WorkoutTitle.normalizeOrDefault("  남산 순환 러닝  ", WorkOutType.RUNNING, 8.2))
                .isEqualTo("남산 순환 러닝");
    }

    @Test
    @DisplayName("지원하지 않는 운동 타입이면 기본 제목 생성에 실패한다")
    void defaultTitleWithoutWorkoutTypeFails() {
        assertThatThrownBy(() -> WorkoutTitle.defaultTitle(null, 8.2))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
