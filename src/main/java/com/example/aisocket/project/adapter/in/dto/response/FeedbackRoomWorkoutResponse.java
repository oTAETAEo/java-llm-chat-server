package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;

import java.time.LocalDateTime;

public record FeedbackRoomWorkoutResponse(
        Long workoutId,
        WorkOutType workOutType,
        AthleteTier tier,
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

    public static FeedbackRoomWorkoutResponse from(FeedbackRoomWorkoutResult result) {
        return new FeedbackRoomWorkoutResponse(
                result.workoutId(),
                result.workOutType(),
                result.tier(),
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
