package com.example.aisocket.project.application.internal.prompt;

import com.example.aisocket.project.application.internal.prompt.embedding.WorkoutEmbeddingPromptStrategy;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkoutEmbeddingPromptBuilder {

    private final Map<WorkOutType, WorkoutEmbeddingPromptStrategy> strategies;

    public WorkoutEmbeddingPromptBuilder(List<WorkoutEmbeddingPromptStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        WorkoutEmbeddingPromptStrategy::supportType,
                        Function.identity()
                ));
    }

    public String build(Workout workout, AthleteTier tier) {
        WorkoutEmbeddingPromptStrategy strategy = strategies.get(workout.getWorkOutType());
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + workout.getWorkOutType());
        }

        return strategy.build(workout, tier);
    }
}
