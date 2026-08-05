package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public interface CoachFeedbackService {

    void generateSingleWorkoutFeedbackStream(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "피드백 방 ID(roomId)는 필수 값입니다.") UUID roomId,
            @NotNull(message = "운동 피드백 요청(command)은 필수 값입니다.") @Valid CoachFeedbackCommand command,
            @NotNull(message = "응답 소비자(chunkConsumer)는 필수 값입니다.") Consumer<String> chunkConsumer
    );

    void generateSingleWorkoutFeedbackStream(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "피드백 방 ID(roomId)는 필수 값입니다.") UUID roomId,
            @NotNull(message = "운동 피드백 요청(command)은 필수 값입니다.") @Valid CoachFeedbackCommand command,
            CreateWorkoutSensorDataCommand sensorCommand,
            @NotNull(message = "응답 소비자(chunkConsumer)는 필수 값입니다.") Consumer<String> chunkConsumer
    );
}
