package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import jakarta.validation.constraints.NotNull;

public interface WorkoutSensorDataRegisterService {

    void register(
            @NotNull(message = "운동 등록 결과(registration)는 필수 값입니다.") WorkoutRecordRegistration registration,
            CreateWorkoutSensorDataCommand command
    );
}
