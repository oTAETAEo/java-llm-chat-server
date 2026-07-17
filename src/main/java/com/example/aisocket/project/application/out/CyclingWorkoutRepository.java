package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;

public interface CyclingWorkoutRepository {

    Long save(CyclingWorkout workout, AthleteTier tier);

}
