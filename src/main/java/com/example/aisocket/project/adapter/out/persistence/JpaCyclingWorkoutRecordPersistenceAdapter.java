package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCyclingWorkoutRecordPersistenceAdapter implements CyclingWorkoutRepository {

    private final JpaCyclingWorkoutRecordRepository repository;

    @Override
    public void save(CyclingWorkout workout, AthleteTier tier) {
        repository.save(CyclingWorkoutRecordEntity.from(workout, tier));
    }
}
