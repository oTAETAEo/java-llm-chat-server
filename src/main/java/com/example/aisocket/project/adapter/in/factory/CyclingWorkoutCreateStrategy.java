package com.example.aisocket.project.adapter.in.factory;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

@Component
public class CyclingWorkoutCreateStrategy implements WorkoutCreateStrategy {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.CYCLING;
    }

    @Override
    public Workout create(Member member, AthleteTier tier, FeedbackRequest request) {
        return CyclingWorkout.create(member, tier, request.toCommonCommand(), request.toCyclingCommand());
    }
}
