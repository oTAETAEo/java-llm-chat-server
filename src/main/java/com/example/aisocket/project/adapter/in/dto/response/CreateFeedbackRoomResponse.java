package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.FeedbackRoomSummaryResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateFeedbackRoomResponse(
        UUID roomId,
        String title,
        boolean pinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CreateFeedbackRoomResponse from(FeedbackRoomSummaryResult result) {
        return new CreateFeedbackRoomResponse(
                result.roomId(),
                result.title(),
                result.pinned(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
