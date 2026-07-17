package com.example.aisocket.project.domain;

import java.util.Map;

public record CreateWorkoutVectorCommand(
        Long memberId,
        Long workoutId,
        WorkOutType workoutType,
        String content,
        Map<String, Object> metadata,
        float[] embedding
) {
}
