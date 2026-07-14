package com.example.aisocket.project.application.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AiSender {

    Mono<String> execute(String promptText);

    Flux<String> sendStream(String promptText);

}
