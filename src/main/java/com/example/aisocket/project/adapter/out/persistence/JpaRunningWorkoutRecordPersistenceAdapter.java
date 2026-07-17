package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaRunningWorkoutRecordPersistenceAdapter implements RunningWorkoutRepository {

    private final JpaRunningWorkoutRecordRepository repository;

    @Override
    public void save(FeedbackRequest request) {
        repository.save(RunningWorkoutRecordEntity.from(request));
    }
}
