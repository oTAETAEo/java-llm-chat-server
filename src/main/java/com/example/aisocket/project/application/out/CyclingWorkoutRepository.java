package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CyclingWorkoutRepository {

    Long save(Member member, CyclingWorkout workout, AthleteTier tier);

    Optional<CyclingWorkout> findByIdAndMemberId(Long workoutId, Long memberId);

    Optional<CyclingWorkout> findDuplicate(Long memberId, CyclingWorkout workout);

    List<CyclingWorkout> findDashboardWorkouts(Long memberId, LocalDateTime from, LocalDateTime toExclusive);

    List<CyclingWorkout> findDashboardHistories(
            Long memberId,
            LocalDateTime from,
            LocalDateTime toExclusive,
            LocalDateTime cursorStartedAt,
            Long cursorWorkoutId,
            int size
    );
}
