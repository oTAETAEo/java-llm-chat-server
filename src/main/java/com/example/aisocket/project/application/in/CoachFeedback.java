package com.example.aisocket.project.application.in;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CoachFeedback {

    Mono<String> getFeedback(Workout workout, AthleteTier tier);

    Flux<String> getFeedbackStream(Workout workout, AthleteTier tier);

}
