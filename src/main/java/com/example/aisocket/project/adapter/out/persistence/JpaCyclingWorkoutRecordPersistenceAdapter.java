package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCyclingWorkoutRecordPersistenceAdapter implements CyclingWorkoutRepository {

    private final JpaCyclingWorkoutRecordRepository repository;

    @Override
    public void save(FeedbackRequest request) {
        repository.save(CyclingWorkoutRecordEntity.from(request));
    }
}
