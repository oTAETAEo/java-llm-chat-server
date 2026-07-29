package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.FeedbackMessageResult;
import com.example.aisocket.project.domain.FeedbackMessageRole;
import com.example.aisocket.project.domain.WorkOutType;

import java.time.LocalDateTime;

public record FeedbackMessageResponse(
        Long messageId,
        FeedbackMessageRole role,
        WorkOutType workOutType,
        Long workoutId,
        String content,
        LocalDateTime createdAt
) {

    public static FeedbackMessageResponse from(FeedbackMessageResult result) {
        return new FeedbackMessageResponse(
                result.messageId(),
                result.role(),
                result.workOutType(),
                result.workoutId(),
                result.content(),
                result.createdAt()
        );
    }
}
