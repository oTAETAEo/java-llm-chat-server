package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CyclingWorkoutRepositoryAdapter implements CyclingWorkoutRepository {

    private final CyclingWorkoutJpaRepository repository;

    @Override
    public Long save(Member member, CyclingWorkout workout, AthleteTier tier) {
        CyclingWorkout savedWorkout = repository.save(workout);

        return savedWorkout.getId();
    }

    @Override
    public Optional<CyclingWorkout> findByIdAndMemberId(Long workoutId, Long memberId) {
        return repository.findByIdAndMemberId(workoutId, memberId);
    }
}
