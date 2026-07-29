package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.FeedbackRoomDetailResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeedbackRoomDetailResponse(
        UUID roomId,
        String title,
        boolean pinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<FeedbackMessageResponse> messages
) {

    public static FeedbackRoomDetailResponse from(FeedbackRoomDetailResult result) {
        return new FeedbackRoomDetailResponse(
                result.roomId(),
                result.title(),
                result.pinned(),
                result.createdAt(),
                result.updatedAt(),
                result.messages().stream()
                        .map(FeedbackMessageResponse::from)
                        .toList()
        );
    }
}
