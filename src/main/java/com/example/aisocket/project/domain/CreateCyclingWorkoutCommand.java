package com.example.aisocket.project.domain;

public record CreateCyclingWorkoutCommand(

        Double avgSpeed,
        Double maxSpeed,
        Double avgPower,
        Double maxPower,
        Double ftp
) {
}
