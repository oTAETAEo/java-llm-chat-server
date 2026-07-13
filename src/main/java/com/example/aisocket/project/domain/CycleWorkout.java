package com.example.aisocket.project.domain;

import lombok.Getter;

@Getter
public class CycleWorkout implements Workout {
    
    private final Double distance;
    private final Double elevGain;
    private final Double elevationMax;
    private final Integer movingTime;
    private final Double calories;
    private final Double avgCadence;
    private final Double maxCadence;
    private final Double maxHeartRate;
    private final Double avgHeartRate;
    
    private final Double avgSpeed;
    private final Double maxSpeed;
    private final Double avgPower;
    private final Double maxPower;
    private final Double ftp;

    public static CycleWorkout createCycleWorkout(CreateWorkoutCommand workoutCommand, CreateCycleWorkoutCommand cycleWorkoutCommand){
        return new CycleWorkout(workoutCommand, cycleWorkoutCommand);
    }

    private CycleWorkout(CreateWorkoutCommand workoutCommand, CreateCycleWorkoutCommand cycleWorkoutCommand) {

        this.distance = workoutCommand.distance();
        this.elevGain = workoutCommand.elevGain();
        this.elevationMax = workoutCommand.elevationMax();
        this.movingTime = workoutCommand.movingTime();
        this.calories = workoutCommand.calories();
        this.avgCadence = workoutCommand.avgCadence();
        this.maxCadence = workoutCommand.maxCadence();
        this.maxHeartRate = workoutCommand.maxHeartRate();
        this.avgHeartRate = workoutCommand.avgHeartRate();

        this.avgSpeed = cycleWorkoutCommand.avgSpeed();
        this.maxSpeed = cycleWorkoutCommand.maxSpeed();
        this.avgPower = cycleWorkoutCommand.avgPower();
        this.maxPower = cycleWorkoutCommand.maxPower();
        this.ftp = cycleWorkoutCommand.ftp();
        
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
        if (ftp != null && ftp < 0) {
            throw new IllegalArgumentException("자전거 FTP는 음수가 될 수 없습니다.");
        }
    }

    @Override
    public WorkOutType getWorkOutType() {
        return WorkOutType.CYCLE;
    }

}