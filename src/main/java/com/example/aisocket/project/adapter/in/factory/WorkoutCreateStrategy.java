package com.example.aisocket.project.adapter.in.factory;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;

public interface WorkoutCreateStrategy {

    WorkOutType supportType();

    Workout create(Member member, AthleteTier tier, FeedbackRequest request);
}
