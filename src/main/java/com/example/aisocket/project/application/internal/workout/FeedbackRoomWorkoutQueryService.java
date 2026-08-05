package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import jakarta.validation.constraints.NotNull;

public interface FeedbackRoomWorkoutQueryService {

    FeedbackRoomWorkoutResult findWorkout(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "피드백 방 운동(roomWorkout)은 필수 값입니다.") FeedbackRoomWorkout roomWorkout
    );
}
