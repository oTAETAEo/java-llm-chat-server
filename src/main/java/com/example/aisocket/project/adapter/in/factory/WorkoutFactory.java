package com.example.aisocket.project.adapter.in.factory;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkoutFactory {

    private final Map<WorkOutType, WorkoutCreateStrategy> strategies;

    public WorkoutFactory(List<WorkoutCreateStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        WorkoutCreateStrategy::supportType,
                        Function.identity()
                ));
    }

    public Workout create(Member member, AthleteTier tier, FeedbackRequest request) {
        if (request.workOutType() == null) {
            throw new IllegalArgumentException("운동 종목(workOutType)은 필수 값입니다.");
        }

        WorkoutCreateStrategy strategy = strategies.get(request.workOutType());
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + request.workOutType());
        }

        return strategy.create(member, tier, request);
    }
}
