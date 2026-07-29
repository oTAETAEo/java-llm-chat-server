package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.FeedbackRoomWorkoutRepository;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeedbackRoomWorkoutRepositoryAdapter implements FeedbackRoomWorkoutRepository {

    private final FeedbackRoomWorkoutJpaRepository repository;

    @Override
    public FeedbackRoomWorkout save(FeedbackRoomWorkout roomWorkout) {
        return repository.save(roomWorkout);
    }

    @Override
    public List<FeedbackRoomWorkout> findByRoomId(UUID roomId) {
        return repository.findByRoomIdOrderByCreatedAtAscIdAsc(roomId);
    }
}
