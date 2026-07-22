package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
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
    public Workout create(Member member, AthleteTier tier, CoachFeedbackCommand command) {
        return CyclingWorkout.create(member, tier, command.commonCommand(), command.cyclingCommand());
    }
}
