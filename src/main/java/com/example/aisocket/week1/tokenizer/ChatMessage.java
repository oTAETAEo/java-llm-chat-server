package com.example.aisocket.week1.tokenizer;

public record ChatMessage(
        Role role,
        String content,
        int tokenCount
) {

    public ChatMessage {
        if (role == null) {
            throw new IllegalArgumentException(TokenizerErrorMessage.ROLE_NOT_NULL.getMessage());
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(TokenizerErrorMessage.CONTENT_NOT_BLANK.getMessage());
        }

        if (tokenCount < 0) {
            throw new IllegalArgumentException(TokenizerErrorMessage.TOKEN_COUNT_NOT_NEGATIVE.getMessage());
        }
    }
}