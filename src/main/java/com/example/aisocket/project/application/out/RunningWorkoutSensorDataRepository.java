package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.RunningWorkout;

import java.util.Optional;

public interface RunningWorkoutSensorDataRepository {

    void save(RunningWorkout workout, CreateWorkoutSensorDataCommand command);

    Optional<String> findSamplesJsonByWorkoutId(Long workoutId);
}
