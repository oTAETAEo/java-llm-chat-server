package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.domain.Workout;

public record WorkoutRecordRegistration(
        Long workoutId,
        Workout workout
) {
}
