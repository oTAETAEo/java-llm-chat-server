package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.in.CoachFeedbackService;
import com.example.aisocket.project.application.internal.prompt.CoachFeedbackPromptBuilder;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.application.out.FeedbackMessageRepository;
import com.example.aisocket.project.application.out.FeedbackRoomRepository;
import com.example.aisocket.project.application.out.FeedbackRoomWorkoutRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.FeedbackMessage;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
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

    private final CoachFeedbackPromptBuilder coachFeedbackPromptBuilder;

    private final AiSender aiSender;

    private final FeedbackRoomRepository feedbackRoomRepository;

    private final FeedbackMessageRepository feedbackMessageRepository;

    private final FeedbackRoomWorkoutRepository feedbackRoomWorkoutRepository;

    @Override
    public void getFeedbackStream(Long memberId, CoachFeedbackCommand command, Consumer<String> chunkConsumer) {
        WorkoutRecordRegistration registration = registerWorkoutAndVector(memberId, command);
        String prompt = coachFeedbackPromptBuilder.build(registration.workout(), command.tier());

        aiSender.sendStream(prompt, chunkConsumer);
    }

    @Override
    public void generateSingleWorkoutFeedbackStream(Long memberId, UUID roomId, CoachFeedbackCommand command, Consumer<String> chunkConsumer) {
        FeedbackRoom room = findOwnedRoom(memberId, roomId);
        WorkoutRecordRegistration registration = registerWorkoutAndVector(memberId, command);

        Long workoutId = registration.workoutId();
        WorkOutType workOutType = registration.workout().getWorkOutType();
        String title = buildTitle(command);
        saveUserWorkoutMessage(room, command, workOutType, workoutId, title);
        feedbackRoomWorkoutRepository.save(FeedbackRoomWorkout.create(room, workOutType, workoutId));

        String prompt = coachFeedbackPromptBuilder.build(registration.workout(), command.tier());
        StringBuilder fullResponse = new StringBuilder();

        aiSender.sendStream(prompt, chunk -> {
            fullResponse.append(chunk);
            chunkConsumer.accept(chunk);
        });

        if (!fullResponse.isEmpty()) {
            feedbackMessageRepository.save(FeedbackMessage.createAssistantMessage(room, workOutType, workoutId, fullResponse.toString()));
        }
    }

    private WorkoutRecordRegistration registerWorkoutAndVector(Long memberId, CoachFeedbackCommand command) {
        WorkoutRecordRegistration registration = workoutRecordRegisterService.register(memberId, command);
        workoutVectorRegisterService.register(registration.member(), registration, command.tier());
        return registration;
    }

    private FeedbackRoom findOwnedRoom(Long memberId, UUID roomId) {
        return feedbackRoomRepository.findByIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new ProjectException(WorkoutErrorCode.FEEDBACK_ROOM_NOT_FOUND));
    }

    private void saveUserWorkoutMessage(
            FeedbackRoom room,
            CoachFeedbackCommand command,
            WorkOutType workOutType,
            Long workoutId,
            String title
    ) {
        room.rename(title);
        feedbackRoomRepository.save(room);
        feedbackMessageRepository.save(FeedbackMessage.createUserWorkoutMessage(room, workOutType, workoutId, buildWorkoutSummary(command)));
    }

    private String buildTitle(CoachFeedbackCommand command) {
        String workoutName = command.workOutType() == WorkOutType.RUNNING ? "러닝" : "자전거";
        Double distance = command.commonCommand().distance();
        if (distance == null) {
            return workoutName + " 피드백";
        }
        return "%s %.1fkm 피드백".formatted(workoutName, distance);
    }

    private String buildWorkoutSummary(CoachFeedbackCommand command) {
        String workoutName = command.workOutType() == WorkOutType.RUNNING ? "러닝" : "자전거";
        return "운동 타입: %s\n등급: %s\n거리: %s km\n활동 시간: %s초".formatted(
                workoutName,
                command.tier(),
                command.commonCommand().distance() == null ? "-" : command.commonCommand().distance(),
                command.commonCommand().movingTime() == null ? "-" : command.commonCommand().movingTime()
        );
    }
}
