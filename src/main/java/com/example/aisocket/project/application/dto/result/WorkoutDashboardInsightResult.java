package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.WorkOutType;

import java.time.DayOfWeek;
import java.util.List;

public record WorkoutDashboardInsightResult(
        WorkoutTypeDistributionResult typeDistribution,
        WorkoutFrequencyResult workoutFrequency,
        FeedbackUsageResult feedbackUsage
) {

    public record WorkoutTypeDistributionResult(
            long totalWorkoutCount,
            double totalDistance,
            WorkoutTypeShareResult running,
            WorkoutTypeShareResult cycling,
            Double avgRunningPace,
            Double avgCyclingPower
    ) {
    }

    public record WorkoutTypeShareResult(
            WorkOutType workOutType,
            long count,
            double distance,
            double workoutRatio,
            double distanceRatio
    ) {
    }

    public record WorkoutFrequencyResult(
            int maxCount,
            List<WorkoutDayFrequencyResult> days
    ) {
    }

    public record WorkoutDayFrequencyResult(
            DayOfWeek dayOfWeek,
            int count
    ) {
    }

    public record FeedbackUsageResult(
            long totalWorkoutCount,
            long feedbackUsedWorkoutCount,
            long totalFeedbackCount,
            double usageRate
    ) {
    }
}
