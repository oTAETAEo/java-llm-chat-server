package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RunningWorkoutRecordRepositoryAdapter implements RunningWorkoutRepository {

    private final RunningWorkoutRecordJpaRepository repository;

    @Override
    public Long save(Member member, RunningWorkout workout, AthleteTier tier) {
        RunningWorkoutRecordEntity savedRecord =
                repository.save(RunningWorkoutRecordEntity.from(workout, tier, member));

        return savedRecord.getId();
    }
}
