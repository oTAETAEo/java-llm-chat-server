package com.example.aisocket.project.adapter.in.mapper;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

@Component
public class CyclingWorkoutRequestMapper implements WorkoutRequestMapper {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.CYCLING;
    }

    @Override
    public Workout toWorkout(FeedbackRequest request) {
        return CyclingWorkout.of(request.toCommonCommand(), request.toCyclingCommand());
    }
}
