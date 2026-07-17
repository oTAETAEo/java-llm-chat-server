package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcRunningWorkoutRecordPersistenceAdapter implements RunningWorkoutRepository {

    private final R2dbcRunningWorkoutRecordRepository repository;

    @Override
    public Mono<Void> save(FeedbackRequest request) {
        return repository.save(RunningWorkoutRecordEntity.from(request)).then();
    }
}
