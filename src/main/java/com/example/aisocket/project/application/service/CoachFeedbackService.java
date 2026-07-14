package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.in.CoachFeedback;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.application.prompt.AiPromptBuilder;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CoachFeedbackService implements CoachFeedback {

    private final AiSender aiSender;

    private final AiPromptBuilder aiPromptBuilder;

    @Override
    public Mono<String> getFeedback(Workout workout, AthleteTier tier) {
        return Mono.fromSupplier(() -> aiPromptBuilder.build(workout, tier))
                .flatMap(aiSender::execute);
    }

    @Override
    public Flux<String> getFeedbackStream(Workout workout, AthleteTier tier) {
        return Mono.fromSupplier(() -> aiPromptBuilder.build(workout, tier))
                .flatMapMany(aiSender::sendStream);
    }

}
