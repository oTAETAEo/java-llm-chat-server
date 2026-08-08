package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.CyclingWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CyclingWorkoutJpaRepository extends JpaRepository<CyclingWorkout, Long> {

    Optional<CyclingWorkout> findByIdAndMemberId(Long id, Long memberId);

    Optional<CyclingWorkout> findFirstByMemberIdAndStartedAtAndEndedAtAndDistanceAndMovingTime(
            Long memberId,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            Double distance,
            Integer movingTime
    );
}
