package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;

import java.util.Optional;
import com.example.aisocket.project.domain.RunningWorkout;

public interface RunningWorkoutRepository {

    Long save(Member member, RunningWorkout workout, AthleteTier tier);

    Optional<RunningWorkout> findByIdAndMemberId(Long workoutId, Long memberId);

}
