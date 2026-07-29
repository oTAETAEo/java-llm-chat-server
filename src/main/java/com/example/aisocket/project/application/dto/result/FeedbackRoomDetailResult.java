package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.FeedbackRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeedbackRoomDetailResult(
        UUID roomId,
        String title,
        boolean pinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<FeedbackMessageResult> messages
) {

    public static FeedbackRoomDetailResult of(FeedbackRoom room, List<FeedbackMessageResult> messages) {
        return new FeedbackRoomDetailResult(
                room.getId(),
                room.getTitle(),
                room.isPinned(),
                room.getCreatedAt(),
                room.getUpdatedAt(),
                messages
        );
    }
}
