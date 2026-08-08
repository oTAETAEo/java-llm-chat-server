package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;

import java.util.Optional;

public interface RunningWorkoutRepository {

    Long save(Member member, RunningWorkout workout, AthleteTier tier);

    Optional<RunningWorkout> findByIdAndMemberId(Long workoutId, Long memberId);

    Optional<RunningWorkout> findDuplicate(Long memberId, RunningWorkout workout);

}
