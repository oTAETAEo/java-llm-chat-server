package com.example.aisocket.project.application.internal.feedback;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.out.FeedbackMessageRepository;
import com.example.aisocket.project.application.out.FeedbackRoomRepository;
import com.example.aisocket.project.application.out.FeedbackRoomWorkoutRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.FeedbackMessage;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class FeedbackRoomRecordServiceImpl implements FeedbackRoomRecordService {

    private final FeedbackRoomRepository feedbackRoomRepository;

    private final FeedbackMessageRepository feedbackMessageRepository;

    private final FeedbackRoomWorkoutRepository feedbackRoomWorkoutRepository;

    @Override
    public FeedbackRoom findOwnedRoom(Long memberId, UUID roomId) {
        return feedbackRoomRepository.findByIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new ProjectException(WorkoutErrorCode.FEEDBACK_ROOM_NOT_FOUND));
    }

    @Override
    public FeedbackRoom createRoom(Member member, String title) {
        return feedbackRoomRepository.create(member, title);
    }

    @Override
    public List<FeedbackRoom> findRecentRooms(Long memberId) {
        return feedbackRoomRepository.findRecentByMemberId(memberId);
    }

    @Override
    public List<FeedbackRoom> findPinnedRooms(Long memberId) {
        return feedbackRoomRepository.findPinnedByMemberId(memberId);
    }

    @Override
    public FeedbackRoom saveRoom(FeedbackRoom room) {
        return feedbackRoomRepository.save(room);
    }

    @Override
    public List<FeedbackMessage> findMessages(UUID roomId) {
        return feedbackMessageRepository.findByRoomId(roomId);
    }

    @Override
    public List<FeedbackRoomWorkout> findRoomWorkouts(UUID roomId) {
        return feedbackRoomWorkoutRepository.findByRoomId(roomId);
    }

    @Override
    public void saveUserWorkoutRecord(
            FeedbackRoom room,
            CoachFeedbackCommand command,
            WorkOutType workOutType,
            Long workoutId
    ) {
        room.rename(buildTitle(command));
        feedbackRoomRepository.save(room);
        feedbackMessageRepository.save(FeedbackMessage.createUserWorkoutMessage(room, workOutType, workoutId, buildWorkoutSummary(command)));
        feedbackRoomWorkoutRepository.save(FeedbackRoomWorkout.create(room, workOutType, workoutId));
    }

    @Override
    public void saveAssistantMessage(FeedbackRoom room, WorkOutType workOutType, Long workoutId, String content) {
        feedbackMessageRepository.save(FeedbackMessage.createAssistantMessage(room, workOutType, workoutId, content));
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
