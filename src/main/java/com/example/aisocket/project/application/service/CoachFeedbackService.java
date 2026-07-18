package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.in.CoachFeedback;
import com.example.aisocket.project.application.in.WorkoutRecordSaver;
import com.example.aisocket.project.application.in.WorkoutVectorSaver;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.application.prompt.AiPromptBuilder;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CoachFeedbackService implements CoachFeedback {

    private final AiSender aiSender;

    private final AiPromptBuilder aiPromptBuilder;

    private final WorkoutRecordSaver workoutRecordSaver;

    private final WorkoutVectorSaver workoutVectorSaver;

    @Override
    public void getFeedbackStream(Member member, Workout workout, AthleteTier tier, Consumer<String> chunkConsumer) {

        Long workoutId = workoutRecordSaver.save(member, workout, tier);

        workoutVectorSaver.save(member, workoutId, workout, tier);

        String prompt = aiPromptBuilder.build(workout, tier);

        aiSender.sendStream(prompt, chunkConsumer);
    }

}
