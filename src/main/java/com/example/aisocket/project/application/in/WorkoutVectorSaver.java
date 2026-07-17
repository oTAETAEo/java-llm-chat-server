package com.example.aisocket.project.application.in;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;

import java.util.UUID;

public interface WorkoutVectorSaver {

    UUID save(Member member, Workout workout, AthleteTier tier);
}
