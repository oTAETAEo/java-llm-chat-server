package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.RunningWorkout;

public interface RunningWorkoutRepository {

    Long save(RunningWorkout workout, AthleteTier tier);

}
