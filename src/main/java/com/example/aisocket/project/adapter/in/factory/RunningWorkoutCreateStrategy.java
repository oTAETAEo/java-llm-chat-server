package com.example.aisocket.project.adapter.in.factory;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

@Component
public class RunningWorkoutCreateStrategy implements WorkoutCreateStrategy {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.RUNNING;
    }

    @Override
    public Workout create(Member member, AthleteTier tier, FeedbackRequest request) {
        return RunningWorkout.create(member, tier, request.toCommonCommand(), request.toRunningCommand());
    }
}
