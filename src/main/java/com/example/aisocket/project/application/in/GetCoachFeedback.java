package com.example.aisocket.project.application.in;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;

public interface GetCoachFeedback {

    String getFeedback(Workout workout, AthleteTier tier);

}
