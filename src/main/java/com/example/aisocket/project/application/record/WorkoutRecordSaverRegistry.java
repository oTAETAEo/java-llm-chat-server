package com.example.aisocket.project.application.record;

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
public class WorkoutRecordSaverRegistry {

    private final Map<WorkOutType, WorkoutRecordSaver> savers;

    public WorkoutRecordSaverRegistry(List<WorkoutRecordSaver> savers) {
        this.savers = savers.stream()
                .collect(Collectors.toMap(
                        WorkoutRecordSaver::supportType,
                        Function.identity()
                ));
    }

    public Long save(Member member, Workout workout, AthleteTier tier) {
        WorkoutRecordSaver saver = savers.get(workout.getWorkOutType());
        if (saver == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + workout.getWorkOutType());
        }

        return saver.save(member, workout, tier);
    }
}
