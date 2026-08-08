package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.domain.WorkOutType;
import jakarta.validation.constraints.NotNull;

public interface WorkoutQueryService {

    FeedbackRoomWorkoutResult getWorkout(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "운동 타입(workOutType)은 필수 값입니다.") WorkOutType workOutType,
            @NotNull(message = "운동 ID(workoutId)는 필수 값입니다.") Long workoutId
    );
}
