package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.RunningWorkoutSensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RunningWorkoutSensorDataJpaRepository extends JpaRepository<RunningWorkoutSensorData, Long> {

    Optional<RunningWorkoutSensorData> findByWorkoutId(Long workoutId);
}
