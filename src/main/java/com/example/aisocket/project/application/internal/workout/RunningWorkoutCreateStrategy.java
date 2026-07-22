package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
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
    public Workout create(Member member, AthleteTier tier, CoachFeedbackCommand command) {
        return RunningWorkout.create(member, tier, command.commonCommand(), command.runningCommand());
    }
}
