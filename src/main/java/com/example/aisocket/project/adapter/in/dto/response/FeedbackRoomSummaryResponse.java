package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.FeedbackRoomSummaryResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record FeedbackRoomSummaryResponse(
        UUID roomId,
        String title,
        boolean pinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static FeedbackRoomSummaryResponse from(FeedbackRoomSummaryResult result) {
        return new FeedbackRoomSummaryResponse(
                result.roomId(),
                result.title(),
                result.pinned(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
