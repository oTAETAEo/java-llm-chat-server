package com.example.aisocket.project.adapter.in.mapper;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;

public interface WorkoutRequestMapper {

    WorkOutType supportType();

    Workout toWorkout(FeedbackRequest request);
}
