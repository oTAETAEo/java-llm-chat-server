package com.example.aisocket.project.application.internal.fit;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateCyclingWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.WorkOutType;

import java.time.LocalDateTime;
import java.util.List;

public record FitParseResult(
        WorkOutType workOutType,
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
        List<FitSensorSample> samples
) {

    public record FitSensorSample(
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

    public CoachFeedbackCommand toCommand(AthleteTier tier) {
        return new CoachFeedbackCommand(
                workOutType,
                tier,
                new CreateCommonWorkoutCommand(
                        startedAt,
                        endedAt,
                        distance,
                        elevGain,
                        elevationMax,
                        movingTime,
                        calories,
                        avgCadence,
                        maxCadence,
                        maxHeartRate,
                        avgHeartRate
                ),
                new CreateRunningWorkoutCommand(avgPace, maxPace, steps),
                new CreateCyclingWorkoutCommand(avgSpeed, maxSpeed, avgPower, maxPower, ftp)
        );
    }
}
