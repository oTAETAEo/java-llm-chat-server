package com.example.aisocket.project.domain;

public record CreateRunningWorkoutCommand(

        Double avgPace,
        Double maxPace,
        Integer steps
) {
}
