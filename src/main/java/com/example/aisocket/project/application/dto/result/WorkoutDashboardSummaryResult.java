package com.example.aisocket.project.application.dto.result;

import java.util.List;

public record WorkoutDashboardSummaryResult(
        long totalWorkoutCount,
        double totalDistance,
        long totalMovingTime,
        long totalFeedbackCount,
        long runningCount,
        long cyclingCount,
        double runningDistance,
        double cyclingDistance,
        Double avgHeartRate,
        double totalElevGain,
        Double avgRunningPace,
        Double avgCyclingPower,
        List<WorkoutDashboardDistanceResult> recentDistances
) {
}
