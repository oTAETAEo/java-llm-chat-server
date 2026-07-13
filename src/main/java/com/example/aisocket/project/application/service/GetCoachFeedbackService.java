package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.in.GetCoachFeedback;
import com.example.aisocket.project.application.out.AiPromptBuilder;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCoachFeedbackService implements GetCoachFeedback {

    private final AiPromptBuilder aiPromptBuilder;

    private final AiSender aiSender;

    @Override
    public String getFeedback(Workout workout, AthleteTier tier) {

        String promptText = aiPromptBuilder.build(workout, tier);

        return aiSender.execute(promptText);
    }

}