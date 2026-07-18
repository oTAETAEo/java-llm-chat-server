package com.example.aisocket.project.application.workout;

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
public class WorkoutSaveStrategyRegistry {

    private final Map<WorkOutType, WorkoutSaveStrategy> savers;

    public WorkoutSaveStrategyRegistry(List<WorkoutSaveStrategy> savers) {
        this.savers = savers.stream()
                .collect(Collectors.toMap(
                        WorkoutSaveStrategy::supportType,
                        Function.identity()
                ));
    }

    public Long save(Member member, Workout workout, AthleteTier tier) {
        WorkoutSaveStrategy saver = savers.get(workout.getWorkOutType());
        if (saver == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + workout.getWorkOutType());
        }

        return saver.save(member, workout, tier);
    }
}
