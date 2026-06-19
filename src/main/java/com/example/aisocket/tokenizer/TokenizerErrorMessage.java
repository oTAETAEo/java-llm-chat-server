package com.example.aisocket.tokenizer;

import lombok.Getter;

public enum TokenizerErrorMessage {

    ROLE_NOT_NULL("role은 null이 아니어야 합니다."),
    CONTENT_NOT_BLANK("content는 비어있을 수 없습니다."),
    TOKEN_COUNT_NOT_NEGATIVE("tokenCount의 개수는 음수일 수 없습니다."),
    MAX_TOKENS_POSITIVE("maxTokens는 0보다 커야 합니다."),
    MESSAGE_NOT_NULL("message는 null일 수 없습니다."),
    SINGLE_MESSAGE_EXCEEDS_MAX_TOKENS("단일 메시지의 토큰 수가 최대 토큰 수를 초과했습니다.");

    @Getter
    private final String message;

    TokenizerErrorMessage(String message) {
        this.message = message;
    }

}