package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.domain.WorkOutType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface WorkoutSensorDataQueryService {

    List<FeedbackRoomWorkoutResult.SensorSampleResult> findSamples(
            @NotNull(message = "운동 타입(workOutType)은 필수 값입니다.") WorkOutType workOutType,
            @NotNull(message = "운동 ID(workoutId)는 필수 값입니다.") Long workoutId
    );
}
