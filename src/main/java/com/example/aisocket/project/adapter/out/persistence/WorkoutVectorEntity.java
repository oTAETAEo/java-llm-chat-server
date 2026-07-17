package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutVector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkoutVectorEntity {

    private final UUID id;
    private final Long memberId;
    private final Long workoutId;
    private final WorkOutType workoutType;
    private final String content;
    private final Map<String, Object> metadata;
    private final float[] embedding;
    private final LocalDateTime createdAt;

    public static WorkoutVectorEntity from(WorkoutVector workoutVector) {
        return new WorkoutVectorEntity(
                workoutVector.getId(),
                workoutVector.getMemberId(),
                workoutVector.getWorkoutId(),
                workoutVector.getWorkoutType(),
                workoutVector.getContent(),
                workoutVector.getMetadata(),
                workoutVector.getEmbedding(),
                workoutVector.getCreatedAt()
        );
    }
}
