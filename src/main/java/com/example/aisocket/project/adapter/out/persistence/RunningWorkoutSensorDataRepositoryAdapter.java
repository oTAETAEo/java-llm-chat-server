package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.RunningWorkoutSensorDataRepository;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutSensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RunningWorkoutSensorDataRepositoryAdapter implements RunningWorkoutSensorDataRepository {

    private final RunningWorkoutSensorDataJpaRepository repository;

    @Override
    public void save(RunningWorkout workout, CreateWorkoutSensorDataCommand command) {
        repository.save(RunningWorkoutSensorData.create(workout, command));
    }

    @Override
    public Optional<String> findSamplesJsonByWorkoutId(Long workoutId) {
        return repository.findByWorkoutId(workoutId)
                .map(RunningWorkoutSensorData::getSamplesJson);
    }
}
