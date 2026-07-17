package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.RunningWorkout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaRunningWorkoutRecordPersistenceAdapter implements RunningWorkoutRepository {

    private final JpaRunningWorkoutRecordRepository repository;

    @Override
    public Long save(RunningWorkout workout, AthleteTier tier) {
        RunningWorkoutRecordEntity savedRecord =
                repository.save(RunningWorkoutRecordEntity.from(workout, tier));

        return savedRecord.getId();
    }
}
