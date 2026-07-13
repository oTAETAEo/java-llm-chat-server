package com.example.aisocket.project.domain;

public interface Workout {

    Double getDistance();
    Double getElevGain();
    Double getElevationMax();
    Integer getMovingTime();
    Double getCalories();
    Double getAvgCadence();
    Double getMaxCadence();
    Double getMaxHeartRate();
    Double getAvgHeartRate();

    void validate();

    WorkOutType getWorkOutType();

}
