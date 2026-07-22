package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.function.Consumer;

public interface CoachFeedbackService {

    void getFeedbackStream(
            @NotNull(message = "회원(member)은 필수 값입니다.") Member member,
            @NotNull(message = "운동 피드백 요청(command)은 필수 값입니다.") @Valid CoachFeedbackCommand command,
            @NotNull(message = "응답 소비자(chunkConsumer)는 필수 값입니다.") Consumer<String> chunkConsumer
    );
}
