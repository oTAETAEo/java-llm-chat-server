package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.FeedbackRoom;

import java.time.LocalDateTime;
import java.util.UUID;

public record FeedbackRoomSummaryResult(
        UUID roomId,
        String title,
        boolean pinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static FeedbackRoomSummaryResult from(FeedbackRoom room) {
        return new FeedbackRoomSummaryResult(
                room.getId(),
                room.getTitle(),
                room.isPinned(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }
}
