package com.example.aisocket.project.domain;

import java.time.LocalDateTime;

public interface Workout {

    LocalDateTime getStartedAt();
    LocalDateTime getEndedAt();
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
