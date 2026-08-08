package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.out.CyclingWorkoutSensorDataRepository;
import com.example.aisocket.project.application.out.RunningWorkoutSensorDataRepository;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.RunningWorkout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class WorkoutSensorDataRegisterServiceImpl implements WorkoutSensorDataRegisterService {

    private final RunningWorkoutSensorDataRepository runningWorkoutSensorDataRepository;

    private final CyclingWorkoutSensorDataRepository cyclingWorkoutSensorDataRepository;

    @Override
    public void register(WorkoutRecordRegistration registration, CreateWorkoutSensorDataCommand command) {
        if (command == null || !registration.created()) {
            return;
        }
        if (registration.workout() instanceof RunningWorkout runningWorkout) {
            runningWorkoutSensorDataRepository.save(runningWorkout, command);
            return;
        }
        if (registration.workout() instanceof CyclingWorkout cyclingWorkout) {
            cyclingWorkoutSensorDataRepository.save(cyclingWorkout, command);
        }
    }
}
