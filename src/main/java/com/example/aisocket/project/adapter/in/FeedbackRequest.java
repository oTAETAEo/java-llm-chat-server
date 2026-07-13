package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.domain.*;

public record FeedbackRequest(

        WorkOutType workOutType,
        AthleteTier tier,

        Double distance,
        Double elevGain,
        Double elevationMax,
        Integer movingTime,
        Double calories,
        Double avgCadence,
        Double maxCadence,
        Double maxHeartRate,
        Double avgHeartRate,

        // 자전거 전용 지표
        Double avgSpeed,
        Double maxSpeed,
        Double avgPower,
        Double maxPower,
        Double ftp,

        // 러닝 전용 지표
        Double avgPace,
        Double maxPace,
        Integer steps
) {

    public Workout toDomain() {
        if (workOutType == null) {
            throw new IllegalArgumentException("운동 종목(workOutType)은 필수 값입니다.");
        }

        CyclingWorkoutCommand commonCommand = createCommonCommand();

        return switch (workOutType) {
            case CYCLING -> CyclingWorkout.of(commonCommand, createCycleCommand());
            case RUNNING -> RunningWorkout.of(commonCommand, createRunningCommand());
        };
    }

    private CyclingWorkoutCommand createCommonCommand() {
        return new CyclingWorkoutCommand(
                distance, elevGain, elevationMax, movingTime, calories,
                avgCadence, maxCadence, maxHeartRate, avgHeartRate
        );
    }

    private CreateCycleWorkoutCommand createCycleCommand() {
        return new CreateCycleWorkoutCommand(
                avgSpeed, maxSpeed, avgPower, maxPower, ftp
        );
    }

    private CreateRunningWorkoutCommand createRunningCommand() {
        return new CreateRunningWorkoutCommand(
                avgPace, maxPace, steps
        );
    }
}