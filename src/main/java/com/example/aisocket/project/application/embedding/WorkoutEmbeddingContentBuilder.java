package com.example.aisocket.project.application.embedding;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;

public interface WorkoutEmbeddingContentBuilder {

    WorkOutType supportType();

    String build(Workout workout, AthleteTier tier);
}
