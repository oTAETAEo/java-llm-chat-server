package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.FeedbackMessage;
import com.example.aisocket.project.domain.FeedbackMessageRole;
import com.example.aisocket.project.domain.WorkOutType;

import java.time.LocalDateTime;

public record FeedbackMessageResult(
        Long messageId,
        FeedbackMessageRole role,
        WorkOutType workOutType,
        Long workoutId,
        String content,
        LocalDateTime createdAt
) {

    public static FeedbackMessageResult from(FeedbackMessage message) {
        return new FeedbackMessageResult(
                message.getId(),
                message.getRole(),
                message.getWorkoutType(),
                message.getWorkoutId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
