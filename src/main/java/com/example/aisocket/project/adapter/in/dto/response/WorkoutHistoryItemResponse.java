package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.WorkoutHistoryItemResult;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;

import java.time.LocalDateTime;

public record WorkoutHistoryItemResponse(
        Long workoutId,
        WorkOutType workOutType,
        AthleteTier tier,
        String title,
        WorkoutInputSource inputSource,
        Long feedbackCount,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Double distance,
        Double elevGain,
        Double elevationMax,
        Integer movingTime,
        Double calories,
        Double avgCadence,
        Double maxCadence,
        Double maxHeartRate,
        Double avgHeartRate,
        Double avgSpeed,
        Double maxSpeed,
        Double avgPower,
        Double maxPower,
        Double ftp,
        Double avgPace,
        Double maxPace,
        Integer steps
) {

    public static WorkoutHistoryItemResponse from(WorkoutHistoryItemResult result) {
        return new WorkoutHistoryItemResponse(
                result.workoutId(),
                result.workOutType(),
                result.tier(),
                result.title(),
                result.inputSource(),
                result.feedbackCount(),
                result.startedAt(),
                result.endedAt(),
                result.distance(),
                result.elevGain(),
                result.elevationMax(),
                result.movingTime(),
                result.calories(),
                result.avgCadence(),
                result.maxCadence(),
                result.maxHeartRate(),
                result.avgHeartRate(),
                result.avgSpeed(),
                result.maxSpeed(),
                result.avgPower(),
                result.maxPower(),
                result.ftp(),
                result.avgPace(),
                result.maxPace(),
                result.steps()
        );
    }
}
