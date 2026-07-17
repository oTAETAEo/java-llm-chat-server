package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.in.CoachFeedback;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.application.prompt.AiPromptBuilder;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CoachFeedbackService implements CoachFeedback {

    private final AiSender aiSender;

    private final AiPromptBuilder aiPromptBuilder;

    @Override
    public String getFeedback(Workout workout, AthleteTier tier) {
        String prompt = aiPromptBuilder.build(workout, tier);
        return aiSender.execute(prompt);
    }

    @Override
    public void getFeedbackStream(Workout workout, AthleteTier tier, Consumer<String> chunkConsumer) {
        String prompt = aiPromptBuilder.build(workout, tier);
        aiSender.sendStream(prompt, chunkConsumer);
    }

}
