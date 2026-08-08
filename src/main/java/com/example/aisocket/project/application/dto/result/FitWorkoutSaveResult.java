package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;

import java.time.LocalDateTime;
import java.util.List;

public record FitWorkoutSaveResult(
        int totalCount,
        int createdCount,
        int duplicatedCount,
        List<SavedWorkoutResult> items
) {

    public static FitWorkoutSaveResult from(List<SavedWorkoutResult> items) {
        int createdCount = (int) items.stream()
                .filter(SavedWorkoutResult::created)
                .count();
        return new FitWorkoutSaveResult(
                items.size(),
                createdCount,
                items.size() - createdCount,
                items
        );
    }

    public record SavedWorkoutResult(
            String fileName,
            Long workoutId,
            WorkOutType workOutType,
            AthleteTier tier,
            String title,
            WorkoutInputSource inputSource,
            boolean created,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            Double distance,
            Integer movingTime
    ) {
    }
}
