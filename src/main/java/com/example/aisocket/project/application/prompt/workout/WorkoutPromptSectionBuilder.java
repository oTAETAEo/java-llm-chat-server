package com.example.aisocket.project.application.prompt.workout;

import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;

public interface WorkoutPromptSectionBuilder {

    WorkOutType supportType();

    String build(Workout workout);
}
