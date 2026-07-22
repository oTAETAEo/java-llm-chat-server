package com.example.aisocket.project.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Getter
public class WorkoutVector extends BaseEntity {

    private final UUID id;

    private final Long memberId;

    private final Long workoutId;

    private final WorkOutType workoutType;

    private final String content;

    private final Map<String, Object> metadata;

    private final float[] embedding;

    public static WorkoutVector create(CreateWorkoutVectorCommand command) {
        return new WorkoutVector(
                command.memberId(),
                command.workoutId(),
                command.workoutType(),
                command.content(),
                command.metadata(),
                command.embedding(),
                LocalDateTime.now()
        );
    }

    private WorkoutVector(
            Long memberId,
            Long workoutId,
            WorkOutType workoutType,
            String content,
            Map<String, Object> metadata,
            float[] embedding,
            LocalDateTime createdAt
    ) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.workoutId = workoutId;
        this.workoutType = workoutType;
        this.content = content;
        this.metadata = metadata == null ? Collections.emptyMap() : Map.copyOf(metadata);
        this.embedding = embedding == null ? new float[0] : embedding.clone();
        initializeCreatedAt(createdAt);

        validate();
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("벡터 ID(id)는 필수 값입니다.");
        }
        if (workoutId == null) {
            throw new IllegalArgumentException("운동 기록 ID(workoutId)는 필수 값입니다.");
        }
        if (workoutType == null) {
            throw new IllegalArgumentException("운동 타입(workoutType)은 필수 값입니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("임베딩 원문(content)은 필수 값입니다.");
        }
        if (embedding.length == 0) {
            throw new IllegalArgumentException("임베딩 벡터(embedding)는 필수 값입니다.");
        }
        if (getCreatedAt() == null) {
            throw new IllegalArgumentException("생성 시각(createdAt)은 필수 값입니다.");
        }
    }

    public float[] getEmbedding() {
        return embedding.clone();
    }
}
