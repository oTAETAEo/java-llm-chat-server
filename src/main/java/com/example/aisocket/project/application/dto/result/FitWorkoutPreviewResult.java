package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;

import java.time.LocalDateTime;
import java.util.List;

public record FitWorkoutPreviewResult(
        WorkOutType workOutType,
        AthleteTier tier,
        String title,
        WorkoutInputSource inputSource,
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
        List<FitSensorSampleResult> samples
) {

    public record FitSensorSampleResult(
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
}
