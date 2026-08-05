package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.CyclingWorkoutSensorDataRepository;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutSensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CyclingWorkoutSensorDataRepositoryAdapter implements CyclingWorkoutSensorDataRepository {

    private final CyclingWorkoutSensorDataJpaRepository repository;

    @Override
    public void save(CyclingWorkout workout, CreateWorkoutSensorDataCommand command) {
        repository.save(CyclingWorkoutSensorData.create(workout, command));
    }

    @Override
    public Optional<String> findSamplesJsonByWorkoutId(Long workoutId) {
        return repository.findByWorkoutId(workoutId)
                .map(CyclingWorkoutSensorData::getSamplesJson);
    }
}
