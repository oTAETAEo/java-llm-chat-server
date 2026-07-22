package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RunningWorkoutRepositoryAdapter implements RunningWorkoutRepository {

    private final RunningWorkoutJpaRepository repository;

    @Override
    public Long save(Member member, RunningWorkout workout, AthleteTier tier) {
        RunningWorkout savedWorkout = repository.save(workout);

        return savedWorkout.getId();
    }
}
