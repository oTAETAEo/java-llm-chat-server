package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;

public interface CyclingWorkoutRepository {

    Long save(Member member, CyclingWorkout workout, AthleteTier tier);

}
