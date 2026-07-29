package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.FeedbackMessage;

import java.util.List;
import java.util.UUID;

public interface FeedbackMessageRepository {

    FeedbackMessage save(FeedbackMessage message);

    List<FeedbackMessage> findByRoomId(UUID roomId);
}
