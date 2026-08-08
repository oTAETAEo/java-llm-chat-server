package com.example.aisocket.project.application.dto.result;

import java.time.LocalDateTime;

public record WorkoutDashboardDistanceResult(
        String label,
        LocalDateTime startedAt,
        Double distance,
        Integer movingTime,
        Double avgHeartRate,
        Double elevGain
) {
}
