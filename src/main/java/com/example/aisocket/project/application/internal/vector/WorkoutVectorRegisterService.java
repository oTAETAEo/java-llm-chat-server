package com.example.aisocket.project.application.internal.vector;

import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;

import java.util.UUID;

public interface WorkoutVectorRegisterService {

    UUID register(Member member, WorkoutRecordRegistration registration, AthleteTier tier);
}
