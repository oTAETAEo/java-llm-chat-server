package com.example.aisocket.project.application.prompt;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;

public record SingleWorkoutPromptContext(
        Workout workout,
        AthleteTier tier
) {
}
