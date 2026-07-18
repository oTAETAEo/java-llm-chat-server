package com.example.aisocket.project.application.record;

import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RunningWorkoutSaver implements WorkoutSaver {

    private final RunningWorkoutRepository runningWorkoutRepository;

    @Override
    public WorkOutType supportType() {
        return WorkOutType.RUNNING;
    }

    @Override
    public Long save(Member member, Workout workout, AthleteTier tier) {
        if (!(workout instanceof RunningWorkout runningWorkout)) {
            throw new IllegalArgumentException("러닝 운동 데이터가 아닙니다.");
        }

        return runningWorkoutRepository.save(member, runningWorkout, tier);
    }
}
