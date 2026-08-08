package com.example.aisocket.project.application.internal.workout;

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
public class WorkoutFeedbackCountServiceImpl implements WorkoutFeedbackCountService {

    private final RunningWorkoutRepository runningWorkoutRepository;

    private final CyclingWorkoutRepository cyclingWorkoutRepository;

    @Override
    @Transactional
    public void increase(Long memberId, WorkOutType workOutType, Long workoutId) {
        if (workOutType == WorkOutType.RUNNING) {
            runningWorkoutRepository.findByIdAndMemberId(workoutId, memberId)
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND))
                    .increaseFeedbackCount();
            return;
        }

        if (workOutType == WorkOutType.CYCLING) {
            cyclingWorkoutRepository.findByIdAndMemberId(workoutId, memberId)
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND))
                    .increaseFeedbackCount();
            return;
        }

        throw new ProjectException(WorkoutErrorCode.UNSUPPORTED_WORKOUT_TYPE);
    }
}
