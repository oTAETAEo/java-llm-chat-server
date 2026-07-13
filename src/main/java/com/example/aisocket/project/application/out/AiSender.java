package com.example.aisocket.project.application.out;

import reactor.core.publisher.Flux;

public interface AiSender {

    String execute(String promptText);

    Flux<String> sendStream(String promptText);

}
