package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.FitWorkoutPreviewResult;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;

import java.time.LocalDateTime;
import java.util.List;

public record FitWorkoutPreviewResponse(
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
        Integer steps,
        List<FitSensorSampleResponse> samples
) {

    public record FitSensorSampleResponse(
            Integer elapsedSeconds,
            Double distance,
            Double latitude,
            Double longitude,
            Double altitude,
            Integer heartRate,
            Integer cadence,
            Double speed,
            Integer power
    ) {
    }

    public static FitWorkoutPreviewResponse from(FitWorkoutPreviewResult result) {
        return new FitWorkoutPreviewResponse(
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
                result.steps(),
                result.samples().stream()
                        .map(sample -> new FitSensorSampleResponse(
                                sample.elapsedSeconds(),
                                sample.distance(),
                                sample.latitude(),
                                sample.longitude(),
                                sample.altitude(),
                                sample.heartRate(),
                                sample.cadence(),
                                sample.speed(),
                                sample.power()
                        ))
                        .toList()
        );
    }
}
