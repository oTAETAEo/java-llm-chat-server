package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;

public interface WorkoutSaveStrategy {

    WorkOutType supportType();

    WorkoutSaveResult save(Member member, Workout workout, AthleteTier tier);
}
