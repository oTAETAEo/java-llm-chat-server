package com.example.aisocket.tokenizer;

public record ChatMessage(
        Role role,
        String content,
        int tokenCount
) {

    public ChatMessage {

        if (role == null) {
            throw new IllegalArgumentException("role은 null이 아니어야 합니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content는 비어있을 수 없습니다.");
        }

        if (tokenCount < 0) {
            throw new IllegalArgumentException("tokenCount의 개수는 음수일 수 없습니다.");
        }
    }

}
