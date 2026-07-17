package com.example.aisocket.project.domain;

public record CreateCommonWorkoutCommand(

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
