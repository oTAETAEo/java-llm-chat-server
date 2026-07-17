package com.example.aisocket.project.application.embedding;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkoutEmbeddingContentBuilderRegistry {

    private final Map<WorkOutType, WorkoutEmbeddingContentBuilder> builders;

    public WorkoutEmbeddingContentBuilderRegistry(List<WorkoutEmbeddingContentBuilder> builders) {
        this.builders = builders.stream()
                .collect(Collectors.toMap(
                        WorkoutEmbeddingContentBuilder::supportType,
                        Function.identity()
                ));
    }

    public String build(Workout workout, AthleteTier tier) {
        WorkoutEmbeddingContentBuilder builder = builders.get(workout.getWorkOutType());
        if (builder == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + workout.getWorkOutType());
        }

        return builder.build(workout, tier);
    }
}
