package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.in.WorkoutRecordSaver;
import com.example.aisocket.project.application.workout.WorkoutSaveStrategyRegistry;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutRecordSaveService implements WorkoutRecordSaver {

    private final WorkoutSaveStrategyRegistry workoutSaveStrategyRegistry;

    @Override
    public Long save(Member member, Workout workout, AthleteTier tier) {
        return workoutSaveStrategyRegistry.save(member, workout, tier);
    }
}
