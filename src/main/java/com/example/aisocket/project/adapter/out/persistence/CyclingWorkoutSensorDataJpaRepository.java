package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.CyclingWorkoutSensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CyclingWorkoutSensorDataJpaRepository extends JpaRepository<CyclingWorkoutSensorData, Long> {

    Optional<CyclingWorkoutSensorData> findByWorkoutId(Long workoutId);
}
