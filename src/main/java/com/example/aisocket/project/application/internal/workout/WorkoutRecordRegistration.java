package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;

public record WorkoutRecordRegistration(
        Long workoutId,
        Member member,
        Workout workout
) {
}
