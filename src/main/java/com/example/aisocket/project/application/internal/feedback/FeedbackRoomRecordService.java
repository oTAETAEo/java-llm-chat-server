package com.example.aisocket.project.application.internal.feedback;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.FeedbackMessage;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface FeedbackRoomRecordService {

    FeedbackRoom findOwnedRoom(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "피드백 방 ID(roomId)는 필수 값입니다.") UUID roomId
    );

    FeedbackRoom createRoom(
            @NotNull(message = "회원(member)는 필수 값입니다.") Member member,
            @NotBlank(message = "피드백 방 제목(title)은 필수 값입니다.") String title
    );

    List<FeedbackRoom> findRecentRooms(@NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId);

    List<FeedbackRoom> findPinnedRooms(@NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId);

    FeedbackRoom saveRoom(@NotNull(message = "피드백 방(room)은 필수 값입니다.") FeedbackRoom room);

    List<FeedbackMessage> findMessages(
            @NotNull(message = "피드백 방 ID(roomId)는 필수 값입니다.") UUID roomId
    );

    List<FeedbackRoomWorkout> findRoomWorkouts(
            @NotNull(message = "피드백 방 ID(roomId)는 필수 값입니다.") UUID roomId
    );

    void saveUserWorkoutRecord(
            @NotNull(message = "피드백 방(room)은 필수 값입니다.") FeedbackRoom room,
            @NotNull(message = "운동 피드백 요청(command)은 필수 값입니다.") @Valid CoachFeedbackCommand command,
            @NotNull(message = "운동 타입(workOutType)은 필수 값입니다.") WorkOutType workOutType,
            @NotNull(message = "운동 ID(workoutId)는 필수 값입니다.") Long workoutId
    );

    void saveAssistantMessage(
            @NotNull(message = "피드백 방(room)은 필수 값입니다.") FeedbackRoom room,
            @NotNull(message = "운동 타입(workOutType)은 필수 값입니다.") WorkOutType workOutType,
            @NotNull(message = "운동 ID(workoutId)는 필수 값입니다.") Long workoutId,
            @NotBlank(message = "AI 피드백 내용(content)은 필수 값입니다.") String content
    );
}
