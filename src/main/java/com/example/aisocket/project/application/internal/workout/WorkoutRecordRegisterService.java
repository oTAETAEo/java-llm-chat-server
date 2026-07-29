package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
public interface WorkoutRecordRegisterService {

    WorkoutRecordRegistration register(Long memberId, CoachFeedbackCommand command);

}
