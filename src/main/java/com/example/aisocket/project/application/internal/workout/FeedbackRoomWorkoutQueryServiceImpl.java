package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class FeedbackRoomWorkoutQueryServiceImpl implements FeedbackRoomWorkoutQueryService {

    private final RunningWorkoutRepository runningWorkoutRepository;

    private final CyclingWorkoutRepository cyclingWorkoutRepository;

    private final WorkoutSensorDataQueryService workoutSensorDataQueryService;

    @Override
    public FeedbackRoomWorkoutResult findWorkout(Long memberId, FeedbackRoomWorkout roomWorkout) {
        if (roomWorkout.getWorkoutType() == WorkOutType.RUNNING) {
            return runningWorkoutRepository.findByIdAndMemberId(roomWorkout.getWorkoutId(), memberId)
                    .map(workout -> FeedbackRoomWorkoutResult.from(
                            workout,
                            workoutSensorDataQueryService.findSamples(WorkOutType.RUNNING, roomWorkout.getWorkoutId())
                    ))
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
        }

        if (roomWorkout.getWorkoutType() == WorkOutType.CYCLING) {
            return cyclingWorkoutRepository.findByIdAndMemberId(roomWorkout.getWorkoutId(), memberId)
                    .map(workout -> FeedbackRoomWorkoutResult.from(
                            workout,
                            workoutSensorDataQueryService.findSamples(WorkOutType.CYCLING, roomWorkout.getWorkoutId())
                    ))
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
        }

        throw new ProjectException(WorkoutErrorCode.UNSUPPORTED_WORKOUT_TYPE);
    }
}
