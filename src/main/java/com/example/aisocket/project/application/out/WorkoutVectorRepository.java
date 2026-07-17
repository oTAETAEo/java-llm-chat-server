package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.WorkoutVector;

import java.util.UUID;

public interface WorkoutVectorRepository {

    UUID save(WorkoutVector workoutVector);
}
