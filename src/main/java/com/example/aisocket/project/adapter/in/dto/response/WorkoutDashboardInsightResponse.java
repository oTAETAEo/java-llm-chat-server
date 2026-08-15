package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult;
import com.example.aisocket.project.domain.WorkOutType;

import java.time.DayOfWeek;
import java.util.List;

public record WorkoutDashboardInsightResponse(
        WorkoutTypeDistributionResponse typeDistribution,
        WorkoutFrequencyResponse workoutFrequency,
        FeedbackUsageResponse feedbackUsage
) {

    public static WorkoutDashboardInsightResponse from(WorkoutDashboardInsightResult result) {
        return new WorkoutDashboardInsightResponse(
                WorkoutTypeDistributionResponse.from(result.typeDistribution()),
                WorkoutFrequencyResponse.from(result.workoutFrequency()),
                FeedbackUsageResponse.from(result.feedbackUsage())
        );
    }

    public record WorkoutTypeDistributionResponse(
            long totalWorkoutCount,
            double totalDistance,
            WorkoutTypeShareResponse running,
            WorkoutTypeShareResponse cycling,
            Double avgRunningPace,
            Double avgCyclingPower
    ) {

        public static WorkoutTypeDistributionResponse from(WorkoutDashboardInsightResult.WorkoutTypeDistributionResult result) {
            return new WorkoutTypeDistributionResponse(
                    result.totalWorkoutCount(),
                    result.totalDistance(),
                    WorkoutTypeShareResponse.from(result.running()),
                    WorkoutTypeShareResponse.from(result.cycling()),
                    result.avgRunningPace(),
                    result.avgCyclingPower()
            );
        }
    }

    public record WorkoutTypeShareResponse(
            WorkOutType workOutType,
            long count,
            double distance,
            double workoutRatio,
            double distanceRatio
    ) {

        public static WorkoutTypeShareResponse from(WorkoutDashboardInsightResult.WorkoutTypeShareResult result) {
            return new WorkoutTypeShareResponse(
                    result.workOutType(),
                    result.count(),
                    result.distance(),
                    result.workoutRatio(),
                    result.distanceRatio()
            );
        }
    }

    public record WorkoutFrequencyResponse(
            int maxCount,
            List<WorkoutDayFrequencyResponse> days
    ) {

        public static WorkoutFrequencyResponse from(WorkoutDashboardInsightResult.WorkoutFrequencyResult result) {
            return new WorkoutFrequencyResponse(
                    result.maxCount(),
                    result.days().stream()
                            .map(WorkoutDayFrequencyResponse::from)
                            .toList()
            );
        }
    }

    public record WorkoutDayFrequencyResponse(
            DayOfWeek dayOfWeek,
            int count
    ) {

        public static WorkoutDayFrequencyResponse from(WorkoutDashboardInsightResult.WorkoutDayFrequencyResult result) {
            return new WorkoutDayFrequencyResponse(result.dayOfWeek(), result.count());
        }
    }

    public record FeedbackUsageResponse(
            long totalWorkoutCount,
            long feedbackUsedWorkoutCount,
            long totalFeedbackCount,
            double usageRate
    ) {

        public static FeedbackUsageResponse from(WorkoutDashboardInsightResult.FeedbackUsageResult result) {
            return new FeedbackUsageResponse(
                    result.totalWorkoutCount(),
                    result.feedbackUsedWorkoutCount(),
                    result.totalFeedbackCount(),
                    result.usageRate()
            );
        }
    }
}
