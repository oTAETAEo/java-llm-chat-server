package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.Member;

public interface WorkoutRecordRegisterService {

    WorkoutRecordRegistration register(Member member, CoachFeedbackCommand command);

}
