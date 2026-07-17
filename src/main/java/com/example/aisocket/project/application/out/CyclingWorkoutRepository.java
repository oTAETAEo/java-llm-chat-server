package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;

public interface CyclingWorkoutRepository {

    void save(CyclingWorkout workout, AthleteTier tier);

}
