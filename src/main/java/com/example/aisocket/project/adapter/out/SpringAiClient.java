package com.example.aisocket.project.adapter.out;

import com.example.aisocket.project.application.out.AiSender;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringAiClient implements AiSender {

    private final OpenAiChatModel chatModel;

    @Override
    public String execute(String promptText) {

        Prompt prompt = new Prompt(promptText);

        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getText();
    }

}
