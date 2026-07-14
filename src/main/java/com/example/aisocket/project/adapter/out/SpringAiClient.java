package com.example.aisocket.project.adapter.out;

import com.example.aisocket.project.application.out.AiSender;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpringAiClient implements AiSender {

    private final OpenAiChatModel chatModel;

    @Override
    public Mono<String> execute(String promptText) {
        return Mono.fromCallable(() -> {
                    Prompt prompt = new Prompt(promptText);
                    ChatResponse response = chatModel.call(prompt);
                    return response.getResult().getOutput().getText();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<String> sendStream(String promptText) {
        Prompt prompt = new Prompt(promptText);

        return chatModel.stream(prompt)
                .flatMap(response -> Optional.ofNullable(response)
                        .map(ChatResponse::getResult)
                        .map(Generation::getOutput)
                        .map(AssistantMessage::getText)
                        .filter(text -> !text.isEmpty())
                        .map(Flux::just)
                        .orElseGet(Flux::empty)
                );
    }

}
