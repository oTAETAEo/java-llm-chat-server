package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CyclingWorkoutRecordRepositoryAdapter implements CyclingWorkoutRepository {

    private final CyclingWorkoutRecordJpaRepository repository;

    @Override
    public Long save(Member member, CyclingWorkout workout, AthleteTier tier) {
        CyclingWorkoutRecordEntity savedRecord =
                repository.save(CyclingWorkoutRecordEntity.from(workout, tier, member));

        return savedRecord.getId();
    }
}
