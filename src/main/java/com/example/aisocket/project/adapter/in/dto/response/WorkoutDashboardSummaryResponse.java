package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.WorkoutDashboardDistanceResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardSummaryResult;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutDashboardSummaryResponse(
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
        List<DistanceResponse> recentDistances
) {

    public static WorkoutDashboardSummaryResponse from(WorkoutDashboardSummaryResult result) {
        return new WorkoutDashboardSummaryResponse(
                result.totalWorkoutCount(),
                result.totalDistance(),
                result.totalMovingTime(),
                result.totalFeedbackCount(),
                result.runningCount(),
                result.cyclingCount(),
                result.runningDistance(),
                result.cyclingDistance(),
                result.avgHeartRate(),
                result.totalElevGain(),
                result.avgRunningPace(),
                result.avgCyclingPower(),
                result.recentDistances().stream()
                        .map(DistanceResponse::from)
                        .toList()
        );
    }

    public record DistanceResponse(
            String label,
            LocalDateTime startedAt,
            Double distance
    ) {

        public static DistanceResponse from(WorkoutDashboardDistanceResult result) {
            return new DistanceResponse(result.label(), result.startedAt(), result.distance());
        }
    }
}
