package com.example.aisocket.project.domain;

import java.time.LocalDateTime;

public record CreateCommonWorkoutCommand(

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
        Double avgHeartRate
) {
}
