package com.example.aisocket.project.application.out;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import reactor.core.publisher.Mono;

public interface RunningWorkoutRepository {

    Mono<Void> save(FeedbackRequest request);

}
