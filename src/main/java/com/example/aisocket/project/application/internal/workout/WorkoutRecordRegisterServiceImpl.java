package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutRecordRegisterServiceImpl implements WorkoutRecordRegisterService {

    private final MemberFinderService memberFinderService;

    private final WorkoutFactory workoutFactory;

    private final WorkoutSaveStrategyRegistry workoutSaveStrategyRegistry;

    @Override
    @Transactional
    public WorkoutRecordRegistration register(Long memberId, CoachFeedbackCommand command) {

        Member member = memberFinderService.findById(memberId);

        Workout workout = workoutFactory.create(member, command.tier(), command);

        WorkoutSaveResult saveResult = workoutSaveStrategyRegistry.save(member, workout, command.tier());

        return new WorkoutRecordRegistration(saveResult.workoutId(), member, workout, saveResult.created());
    }
}
