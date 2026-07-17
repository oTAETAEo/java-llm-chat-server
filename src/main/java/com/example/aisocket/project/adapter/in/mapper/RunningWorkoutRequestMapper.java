package com.example.aisocket.project.adapter.in.mapper;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

@Component
public class RunningWorkoutRequestMapper implements WorkoutRequestMapper {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.RUNNING;
    }

    @Override
    public Workout toWorkout(FeedbackRequest request) {
        return RunningWorkout.of(request.toCommonCommand(), request.toRunningCommand());
    }
}
