package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.in.CoachFeedbackService;
import com.example.aisocket.project.application.internal.prompt.CoachFeedbackPromptBuilder;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.function.Consumer;

@Service
@Validated
@RequiredArgsConstructor
public class CoachFeedbackServiceImpl implements CoachFeedbackService {

    private final WorkoutRecordRegisterService workoutRecordRegisterService;

    private final WorkoutVectorRegisterService workoutVectorRegisterService;

    private final CoachFeedbackPromptBuilder coachFeedbackPromptBuilder;

    private final AiSender aiSender;

    @Override
    public void getFeedbackStream(Member member, CoachFeedbackCommand command, Consumer<String> chunkConsumer) {
        WorkoutRecordRegistration registration = workoutRecordRegisterService.register(member, command);

        workoutVectorRegisterService.register(member, registration, command.tier());

        String prompt = coachFeedbackPromptBuilder.build(registration.workout(), command.tier());

        aiSender.sendStream(prompt, chunkConsumer);
    }
}
