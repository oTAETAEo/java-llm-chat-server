package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.FeedbackMessageRepository;
import com.example.aisocket.project.domain.FeedbackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeedbackMessageRepositoryAdapter implements FeedbackMessageRepository {

    private final FeedbackMessageJpaRepository repository;

    @Override
    public FeedbackMessage save(FeedbackMessage message) {
        return repository.save(message);
    }

    @Override
    public List<FeedbackMessage> findByRoomId(UUID roomId) {
        return repository.findByRoomIdOrderByCreatedAtAscIdAsc(roomId);
    }
}
