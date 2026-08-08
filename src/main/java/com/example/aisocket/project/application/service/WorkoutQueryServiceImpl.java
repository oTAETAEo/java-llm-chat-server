package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.in.WorkoutQueryService;
import com.example.aisocket.project.application.internal.workout.WorkoutSensorDataQueryService;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.WorkOutType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class WorkoutQueryServiceImpl implements WorkoutQueryService {

    private final RunningWorkoutRepository runningWorkoutRepository;
    private final CyclingWorkoutRepository cyclingWorkoutRepository;
    private final WorkoutSensorDataQueryService workoutSensorDataQueryService;

    @Override
    @Transactional(readOnly = true)
    public FeedbackRoomWorkoutResult getWorkout(Long memberId, WorkOutType workOutType, Long workoutId) {
        return switch (workOutType) {
            case RUNNING -> runningWorkoutRepository.findByIdAndMemberId(workoutId, memberId)
                    .map(workout -> FeedbackRoomWorkoutResult.from(
                            workout,
                            workoutSensorDataQueryService.findSamples(WorkOutType.RUNNING, workout.getId())
                    ))
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
            case CYCLING -> cyclingWorkoutRepository.findByIdAndMemberId(workoutId, memberId)
                    .map(workout -> FeedbackRoomWorkoutResult.from(
                            workout,
                            workoutSensorDataQueryService.findSamples(WorkOutType.CYCLING, workout.getId())
                    ))
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
        };
    }
}
