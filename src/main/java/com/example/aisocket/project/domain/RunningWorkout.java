package com.example.aisocket.project.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RunningWorkout implements Workout {

    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
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

    public static RunningWorkout of(CreateCommonWorkoutCommand workoutCommand, CreateRunningWorkoutCommand runningWorkoutCommand){
        return new RunningWorkout(workoutCommand, runningWorkoutCommand);
    }

    private RunningWorkout(CreateCommonWorkoutCommand workoutCommand, CreateRunningWorkoutCommand runningWorkoutCommand) {

        this.startedAt = workoutCommand.startedAt();
        this.endedAt = workoutCommand.endedAt();
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
        if (startedAt == null) {
            throw new IllegalArgumentException("운동 시작 시간(startedAt)은 필수 값입니다.");
        }
        if (endedAt == null) {
            throw new IllegalArgumentException("운동 종료 시간(endedAt)은 필수 값입니다.");
        }
        if (endedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("운동 종료 시간(endedAt)은 시작 시간(startedAt)보다 빠를 수 없습니다.");
        }
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
