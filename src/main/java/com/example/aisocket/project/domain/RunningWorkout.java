package com.example.aisocket.project.domain;

import lombok.Getter;

@Getter
public class RunningWorkout implements Workout {

    private final Double distance;
    private final Double elevGain;
    private final Double elevationMax;
    private final Integer movingTime;
    private final Double calories;
    private final Double avgCadence;
    private final Double maxCadence;
    private final Double maxHeartRate;
    private final Double avgHeartRate;
    private final Double avgPace;
    private final Double maxPace;
    private final Integer steps;

    public static RunningWorkout of(CommonWorkoutCommand workoutCommand, CreateRunningWorkoutCommand runningWorkoutCommand){
        return new RunningWorkout(workoutCommand, runningWorkoutCommand);
    }

    private RunningWorkout(CommonWorkoutCommand workoutCommand, CreateRunningWorkoutCommand runningWorkoutCommand) {

        this.distance = workoutCommand.distance();
        this.elevGain = workoutCommand.elevGain();
        this.elevationMax = workoutCommand.elevationMax();
        this.movingTime = workoutCommand.movingTime();
        this.calories = workoutCommand.calories();
        this.avgCadence = workoutCommand.avgCadence();
        this.maxCadence = workoutCommand.maxCadence();
        this.maxHeartRate = workoutCommand.maxHeartRate();
        this.avgHeartRate = workoutCommand.avgHeartRate();

        this.avgPace = runningWorkoutCommand.avgPace();
        this.maxPace = runningWorkoutCommand.maxPace();
        this.steps = runningWorkoutCommand.steps();

        validate();
    }

    @Override
    public void validate() {
        if (movingTime != null && movingTime <= 0) {
            throw new IllegalArgumentException("운동 시간(movingTime)은 0보다 커야 합니다.");
        }
        if (distance != null && distance < 0) {
            throw new IllegalArgumentException("운동 거리(distance)는 음수가 될 수 없습니다.");
        }
        if (steps != null && steps < 0) {
            throw new IllegalArgumentException("걸음 수(steps)는 음수가 될 수 없습니다.");
        }
    }

    @Override
    public WorkOutType getWorkOutType() {
        return WorkOutType.RUNNING;
    }

}