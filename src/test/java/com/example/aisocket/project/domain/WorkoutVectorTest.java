package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkoutVectorTest {

    @Test
    @DisplayName("운동 벡터를 생성한다")
    void createWorkoutVector() {
        WorkoutVector workoutVector = WorkoutVector.create(new CreateWorkoutVectorCommand(
                1L,
                10L,
                WorkOutType.RUNNING,
                "러닝 운동 기록",
                Map.of("workoutType", WorkOutType.RUNNING.name()),
                new float[]{0.1f, 0.2f, 0.3f}
        ));

        assertThat(workoutVector.getId()).isNotNull();
        assertThat(workoutVector.getMemberId()).isEqualTo(1L);
        assertThat(workoutVector.getWorkoutId()).isEqualTo(10L);
        assertThat(workoutVector.getWorkoutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(workoutVector.getContent()).isEqualTo("러닝 운동 기록");
        assertThat(workoutVector.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("운동 기록 ID가 없으면 운동 벡터 생성에 실패한다")
    void createWithoutWorkoutIdFails() {
        assertThatThrownBy(() -> WorkoutVector.create(command(null, WorkOutType.RUNNING, "content", new float[]{0.1f})))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 기록 ID");
    }

    @Test
    @DisplayName("운동 타입이 없으면 운동 벡터 생성에 실패한다")
    void createWithoutWorkoutTypeFails() {
        assertThatThrownBy(() -> WorkoutVector.create(command(10L, null, "content", new float[]{0.1f})))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("운동 타입");
    }

    @Test
    @DisplayName("임베딩 원문이 없으면 운동 벡터 생성에 실패한다")
    void createWithoutContentFails() {
        assertThatThrownBy(() -> WorkoutVector.create(command(10L, WorkOutType.RUNNING, " ", new float[]{0.1f})))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("임베딩 원문");
    }

    @Test
    @DisplayName("임베딩 벡터가 없으면 운동 벡터 생성에 실패한다")
    void createWithoutEmbeddingFails() {
        assertThatThrownBy(() -> WorkoutVector.create(command(10L, WorkOutType.RUNNING, "content", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("임베딩 벡터");
    }

    @Test
    @DisplayName("임베딩 벡터는 방어적으로 복사한다")
    void copyEmbeddingDefensively() {
        float[] embedding = new float[]{0.1f, 0.2f, 0.3f};
        WorkoutVector workoutVector = WorkoutVector.create(command(10L, WorkOutType.RUNNING, "content", embedding));

        embedding[0] = 9.9f;
        float[] returnedEmbedding = workoutVector.getEmbedding();
        returnedEmbedding[1] = 8.8f;

        assertThat(workoutVector.getEmbedding()).containsExactly(0.1f, 0.2f, 0.3f);
    }

    private CreateWorkoutVectorCommand command(Long workoutId, WorkOutType workoutType, String content, float[] embedding) {
        return new CreateWorkoutVectorCommand(
                1L,
                workoutId,
                workoutType,
                content,
                Map.of("source", "test"),
                embedding
        );
    }
}
