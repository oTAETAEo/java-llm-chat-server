package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.FitWorkoutSaveResult;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;

import java.time.LocalDateTime;
import java.util.List;

public record FitWorkoutSaveResponse(
        int totalCount,
        int createdCount,
        int duplicatedCount,
        List<SavedWorkoutResponse> items
) {

    public static FitWorkoutSaveResponse from(FitWorkoutSaveResult result) {
        return new FitWorkoutSaveResponse(
                result.totalCount(),
                result.createdCount(),
                result.duplicatedCount(),
                result.items().stream()
                        .map(SavedWorkoutResponse::from)
                        .toList()
        );
    }

    public record SavedWorkoutResponse(
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

        public static SavedWorkoutResponse from(FitWorkoutSaveResult.SavedWorkoutResult result) {
            return new SavedWorkoutResponse(
                    result.fileName(),
                    result.workoutId(),
                    result.workOutType(),
                    result.tier(),
                    result.title(),
                    result.inputSource(),
                    result.created(),
                    result.startedAt(),
                    result.endedAt(),
                    result.distance(),
                    result.movingTime()
            );
        }
    }
}
