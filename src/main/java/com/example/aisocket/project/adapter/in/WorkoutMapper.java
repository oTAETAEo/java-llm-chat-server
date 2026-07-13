package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

@Component
public class WorkoutMapper {

    public Workout toWorkout(FeedbackRequest request) {
        if (request.workOutType() == null) {
            throw new IllegalArgumentException("운동 종목(workOutType)은 필수 값입니다.");
        }

        return switch (request.workOutType()) {
            case CYCLING -> CyclingWorkout.of(request.toCommonCommand(), request.toCyclingCommand());
            case RUNNING -> RunningWorkout.of(request.toCommonCommand(), request.toRunningCommand());
        };
    }
}
