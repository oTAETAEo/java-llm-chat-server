package com.example.aisocket.project.domain;

public record CreateCycleWorkoutCommand(

        Double avgSpeed,
        Double maxSpeed,
        Double avgPower,
        Double maxPower,
        Double ftp
) {
}
