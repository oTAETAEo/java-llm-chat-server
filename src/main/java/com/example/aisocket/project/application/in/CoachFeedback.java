package com.example.aisocket.project.application.in;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;

import java.util.function.Consumer;

public interface CoachFeedback {

    void getFeedbackStream(Member member, Workout workout, AthleteTier tier, Consumer<String> chunkConsumer);

}
