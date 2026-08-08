package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CyclingWorkoutSaveStrategy implements WorkoutSaveStrategy {

    private final CyclingWorkoutRepository cyclingWorkoutRepository;

    @Override
    public WorkOutType supportType() {
        return WorkOutType.CYCLING;
    }

    @Override
    public WorkoutSaveResult save(Member member, Workout workout, AthleteTier tier) {
        if (!(workout instanceof CyclingWorkout cyclingWorkout)) {
            throw new IllegalArgumentException("자전거 운동 데이터가 아닙니다.");
        }

        return cyclingWorkoutRepository.findDuplicate(member.getId(), cyclingWorkout)
                .map(existingWorkout -> new WorkoutSaveResult(existingWorkout.getId(), false))
                .orElseGet(() -> new WorkoutSaveResult(
                        cyclingWorkoutRepository.save(member, cyclingWorkout, tier),
                        true
                ));
    }
}
