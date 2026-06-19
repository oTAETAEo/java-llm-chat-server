package com.example.aisocket.tokenizer;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TokenSlidingWindow {

    @Getter
    private final int maxTokens;

    private final Deque<ChatMessage> messages = new ArrayDeque<>();

    @Getter
    private int currentTokens = 0;

    private TokenSlidingWindow(int maxTokens) {
        if (maxTokens <= 0) {
            throw new IllegalArgumentException(TokenizerErrorMessage.MAX_TOKENS_POSITIVE.getMessage());
        }

        this.maxTokens = maxTokens;
    }

    public static TokenSlidingWindow create(int maxTokens) {
        return new TokenSlidingWindow(maxTokens);
    }

    public void addMessage(ChatMessage message) {
        validateMessage(message);

        trimBeforeAdd(message.tokenCount());

        messages.addLast(message);
        currentTokens += message.tokenCount();
    }

    public String toPrompt() {
        StringBuilder sb = new StringBuilder();

        for (ChatMessage message : messages) {
            sb.append(message.role())
                    .append(": ")
                    .append(message.content())
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

    public int size() {
        return messages.size();
    }

    public List<ChatMessage> getMessages() {
        return List.copyOf(messages);
    }


    private void validateMessage(ChatMessage message) {

        if (message == null) {
            throw new IllegalArgumentException(TokenizerErrorMessage.MESSAGE_NOT_NULL.getMessage());
        }

        if (message.tokenCount() > maxTokens) {
            throw new IllegalArgumentException(TokenizerErrorMessage.SINGLE_MESSAGE_EXCEEDS_MAX_TOKENS.getMessage());
        }
    }

    private void trimBeforeAdd(int newMessageTokens) {
        while (currentTokens + newMessageTokens > maxTokens) {
            ChatMessage removed = messages.removeFirst();
            currentTokens -= removed.tokenCount();
        }
    }

}