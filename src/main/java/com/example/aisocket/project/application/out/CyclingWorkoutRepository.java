package com.example.aisocket.project.application.out;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import reactor.core.publisher.Mono;

public interface CyclingWorkoutRepository {

    Mono<Void> save(FeedbackRequest request);

}
