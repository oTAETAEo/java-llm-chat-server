package com.example.aisocket.project.application.workout;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;

public interface WorkoutSaveStrategy {

    WorkOutType supportType();

    Long save(Member member, Workout workout, AthleteTier tier);
}
