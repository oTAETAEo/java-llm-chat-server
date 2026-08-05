package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.CyclingWorkout;

import java.util.Optional;

public interface CyclingWorkoutSensorDataRepository {

    void save(CyclingWorkout workout, CreateWorkoutSensorDataCommand command);

    Optional<String> findSamplesJsonByWorkoutId(Long workoutId);
}
