package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;

import java.time.LocalDateTime;
import java.util.List;

public record FeedbackRoomWorkoutResult(
        Long workoutId,
        WorkOutType workOutType,
        AthleteTier tier,
        String title,
        WorkoutInputSource inputSource,
        Long feedbackCount,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Double distance,
        Double elevGain,
        Double elevationMax,
        Integer movingTime,
        Double calories,
        Double avgCadence,
        Double maxCadence,
        Double maxHeartRate,
        Double avgHeartRate,
        Double avgSpeed,
        Double maxSpeed,
        Double avgPower,
        Double maxPower,
        Double ftp,
        Double avgPace,
        Double maxPace,
        Integer steps,
        List<SensorSampleResult> samples
) {

    public record SensorSampleResult(
            Integer elapsedSeconds,
            Double distance,
            Double latitude,
            Double longitude,
            Double altitude,
            Integer heartRate,
            Integer cadence,
            Double speed,
            Integer power
    ) {
    }

    public static FeedbackRoomWorkoutResult from(RunningWorkout workout) {
        return from(workout, List.of());
    }

    public static FeedbackRoomWorkoutResult from(RunningWorkout workout, List<SensorSampleResult> samples) {
        return new FeedbackRoomWorkoutResult(
                workout.getId(),
                workout.getWorkOutType(),
                workout.getTier(),
                workout.getTitle(),
                workout.getInputSource(),
                workout.getFeedbackCount(),
                workout.getStartedAt(),
                workout.getEndedAt(),
                workout.getDistance(),
                workout.getElevGain(),
                workout.getElevationMax(),
                workout.getMovingTime(),
                workout.getCalories(),
                workout.getAvgCadence(),
                workout.getMaxCadence(),
                workout.getMaxHeartRate(),
                workout.getAvgHeartRate(),
                null,
                null,
                null,
                null,
                null,
                workout.getAvgPace(),
                workout.getMaxPace(),
                workout.getSteps(),
                samples
        );
    }

    public static FeedbackRoomWorkoutResult from(CyclingWorkout workout) {
        return from(workout, List.of());
    }

    public static FeedbackRoomWorkoutResult from(CyclingWorkout workout, List<SensorSampleResult> samples) {
        return new FeedbackRoomWorkoutResult(
                workout.getId(),
                workout.getWorkOutType(),
                workout.getTier(),
                workout.getTitle(),
                workout.getInputSource(),
                workout.getFeedbackCount(),
                workout.getStartedAt(),
                workout.getEndedAt(),
                workout.getDistance(),
                workout.getElevGain(),
                workout.getElevationMax(),
                workout.getMovingTime(),
                workout.getCalories(),
                workout.getAvgCadence(),
                workout.getMaxCadence(),
                workout.getMaxHeartRate(),
                workout.getAvgHeartRate(),
                workout.getAvgSpeed(),
                workout.getMaxSpeed(),
                workout.getAvgPower(),
                workout.getMaxPower(),
                workout.getFtp(),
                null,
                null,
                null,
                samples
        );
    }
}
