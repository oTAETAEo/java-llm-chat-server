package com.example.aisocket.week1.tokenizer;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TokenSlidingWindow {

    @Getter
    private final int maxTokens;

    @Getter
    private final int maxSingleMessageTokens;

    private final Deque<ChatMessage> messages = new ArrayDeque<>();

    @Getter
    private int currentTokens = 0;

    private TokenSlidingWindow(int maxTokens, int maxSingleMessageTokens) {
        if (maxTokens <= 0 || maxSingleMessageTokens <= 0) {
            throw new IllegalArgumentException(TokenizerErrorMessage.MAX_TOKENS_POSITIVE.getMessage());
        }
        this.maxTokens = maxTokens;
        this.maxSingleMessageTokens = maxSingleMessageTokens;
    }

    public static TokenSlidingWindow create(int maxTokens, int maxSingleMessageTokens) {
        return new TokenSlidingWindow(maxTokens, maxSingleMessageTokens);
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

        // 💡 수정 완료: 단일 메시지 한도는 maxSingleMessageTokens로 철저히 격리하여 검증합니다.
        if (message.tokenCount() > maxSingleMessageTokens) {
            throw new IllegalArgumentException(TokenizerErrorMessage.SINGLE_MESSAGE_EXCEEDS_MAX_TOKENS.getMessage());
        }
    }

    private void trimBeforeAdd(int newMessageTokens) {
        while (currentTokens + newMessageTokens > maxTokens) {
            ChatMessage removed = messages.removeFirst();
            currentTokens -= removed.tokenCount();

            System.out.println("[컨텍스트 정리] 슬라이딩 윈도우 한도 초과로 이전 대화 자동 삭제: " + removed.content());
        }
    }
}