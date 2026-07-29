package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.RunningWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RunningWorkoutJpaRepository extends JpaRepository<RunningWorkout, Long> {

    Optional<RunningWorkout> findByIdAndMemberId(Long id, Long memberId);
}
