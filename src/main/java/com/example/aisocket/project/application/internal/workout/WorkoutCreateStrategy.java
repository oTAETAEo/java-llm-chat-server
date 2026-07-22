package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;

public interface WorkoutCreateStrategy {

    WorkOutType supportType();

    Workout create(Member member, AthleteTier tier, CoachFeedbackCommand command);
}
