package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutRecordRegisterServiceImpl implements WorkoutRecordRegisterService {

    private final WorkoutFactory workoutFactory;

    private final WorkoutSaveStrategyRegistry workoutSaveStrategyRegistry;

    @Override
    @Transactional
    public WorkoutRecordRegistration register(Member member, CoachFeedbackCommand command) {

        Workout workout = workoutFactory.create(member, command.tier(), command);

        Long workoutId = workoutSaveStrategyRegistry.save(member, workout, command.tier());

        return new WorkoutRecordRegistration(workoutId, workout);
    }
}
