package com.example.aisocket.project.adapter.out;

import com.example.aisocket.project.application.out.AiSender;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class SpringAiClient implements AiSender {

    private final OpenAiChatModel chatModel;

    @Override
    public void sendStream(String promptText, Consumer<String> chunkConsumer) {
        Prompt prompt = new Prompt(promptText);

        chatModel.stream(prompt)
                .toIterable()
                .forEach(response -> Optional.ofNullable(response)
                        .map(ChatResponse::getResult)
                        .map(Generation::getOutput)
                        .map(AssistantMessage::getText)
                        .filter(text -> !text.isEmpty())
                        .ifPresent(chunkConsumer)
                );
    }

}
