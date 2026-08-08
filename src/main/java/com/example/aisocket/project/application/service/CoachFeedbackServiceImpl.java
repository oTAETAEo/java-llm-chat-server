package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.in.CoachFeedbackService;
import com.example.aisocket.project.application.internal.feedback.FeedbackRoomRecordService;
import com.example.aisocket.project.application.internal.prompt.CoachFeedbackPromptBuilder;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.internal.workout.WorkoutFeedbackCountService;
import com.example.aisocket.project.application.internal.workout.WorkoutSensorDataRegisterService;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.WorkOutType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@Validated
@RequiredArgsConstructor
public class CoachFeedbackServiceImpl implements CoachFeedbackService {

    private final WorkoutRecordRegisterService workoutRecordRegisterService;

    private final WorkoutVectorRegisterService workoutVectorRegisterService;

    private final WorkoutSensorDataRegisterService workoutSensorDataRegisterService;

    private final WorkoutFeedbackCountService workoutFeedbackCountService;

    private final FeedbackRoomRecordService feedbackRoomRecordService;

    private final CoachFeedbackPromptBuilder coachFeedbackPromptBuilder;

    private final AiSender aiSender;

    @Override
    public void generateSingleWorkoutFeedbackStream(Long memberId, UUID roomId, CoachFeedbackCommand command, Consumer<String> chunkConsumer) {
        generateSingleWorkoutFeedbackStream(memberId, roomId, command, null, chunkConsumer);
    }

    @Override
    public void generateSingleWorkoutFeedbackStream(
            Long memberId,
            UUID roomId,
            CoachFeedbackCommand command,
            CreateWorkoutSensorDataCommand sensorCommand,
            Consumer<String> chunkConsumer
    ) {
        FeedbackRoom room = feedbackRoomRecordService.findOwnedRoom(memberId, roomId);
        WorkoutRecordRegistration registration = registerWorkoutAndVector(memberId, command);
        workoutSensorDataRegisterService.register(registration, sensorCommand);

        Long workoutId = registration.workoutId();
        WorkOutType workOutType = registration.workout().getWorkOutType();
        feedbackRoomRecordService.saveUserWorkoutRecord(room, command, workOutType, workoutId);
        workoutFeedbackCountService.increase(memberId, workOutType, workoutId);

        String prompt = coachFeedbackPromptBuilder.build(registration.workout(), command.tier());
        StringBuilder fullResponse = new StringBuilder();

        aiSender.sendStream(prompt, chunk -> {
            fullResponse.append(chunk);
            chunkConsumer.accept(chunk);
        });

        if (!fullResponse.isEmpty()) {
            feedbackRoomRecordService.saveAssistantMessage(room, workOutType, workoutId, fullResponse.toString());
        }
    }

    private WorkoutRecordRegistration registerWorkoutAndVector(Long memberId, CoachFeedbackCommand command) {
        WorkoutRecordRegistration registration = workoutRecordRegisterService.register(memberId, command);
        if (registration.created()) {
            workoutVectorRegisterService.register(registration.member(), registration, command.tier());
        }
        return registration;
    }

}
