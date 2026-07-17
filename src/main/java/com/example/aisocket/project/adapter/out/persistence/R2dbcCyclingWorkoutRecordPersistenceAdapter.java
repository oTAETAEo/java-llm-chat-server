package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcCyclingWorkoutRecordPersistenceAdapter implements CyclingWorkoutRepository {

    private final R2dbcCyclingWorkoutRecordRepository repository;

    @Override
    public Mono<Void> save(FeedbackRequest request) {
        return repository.save(CyclingWorkoutRecordEntity.from(request)).then();
    }
}
