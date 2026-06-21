package com.example.aisocket.tokenizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenSlidingWindowTest {

    @Test
    @DisplayName("메시지를 추가하면 현재 토큰 수가 증가한다")
    void addMessage() {
        TokenSlidingWindow window = TokenSlidingWindow.create(30, 50);

        ChatMessage message = new ChatMessage(Role.USER, "JVM이 뭐야?", 5);

        window.addMessage(message);

        assertThat(window.getCurrentTokens()).isEqualTo(5);
        assertThat(window.size()).isEqualTo(1);
        assertThat(window.getMessages()).containsExactly(message);
    }

    @Test
    @DisplayName("최대 토큰 수를 초과하면 오래된 메시지부터 제거한다")
    void removeOldMessagesWhenExceedMaxTokens() {
        TokenSlidingWindow window = TokenSlidingWindow.create(30, 50);

        ChatMessage m1 = new ChatMessage(Role.USER, "첫 번째 메시지", 10);
        ChatMessage m2 = new ChatMessage(Role.ASSISTANT, "두 번째 메시지", 15);
        ChatMessage m3 = new ChatMessage(Role.USER, "세 번째 메시지", 10);

        window.addMessage(m1);
        window.addMessage(m2);
        window.addMessage(m3);

        assertThat(window.getCurrentTokens()).isEqualTo(25);
        assertThat(window.getMessages()).containsExactly(m2, m3);
    }

    @Test
    @DisplayName("메시지 추가 후 현재 토큰 수는 최대 토큰 수를 넘지 않는다")
    void currentTokensDoesNotExceedMaxTokensAfterAddMessage() {
        TokenSlidingWindow window = TokenSlidingWindow.create(30, 50);

        ChatMessage m1 = new ChatMessage(Role.USER, "첫 번째 메시지", 10);
        ChatMessage m2 = new ChatMessage(Role.ASSISTANT, "두 번째 메시지", 15);
        ChatMessage m3 = new ChatMessage(Role.USER, "세 번째 메시지", 10);

        window.addMessage(m1);
        window.addMessage(m2);
        window.addMessage(m3);

        assertThat(window.getCurrentTokens()).isLessThanOrEqualTo(window.getMaxTokens());
    }

    @Test
    @DisplayName("단일 메시지가 최대 토큰 수를 초과하면 예외가 발생한다")
    void throwExceptionWhenSingleMessageExceedsMaxTokens() {
        TokenSlidingWindow window = TokenSlidingWindow.create(30, 50);

        ChatMessage message = new ChatMessage(Role.USER, "너무 긴 메시지", 31);

        assertThatThrownBy(() -> window.addMessage(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(TokenizerErrorMessage.SINGLE_MESSAGE_EXCEEDS_MAX_TOKENS.getMessage());
    }

    @Test
    @DisplayName("최대 토큰 수는 0보다 커야 한다")
    void throwExceptionWhenMaxTokensIsZeroOrNegative() {
        assertThatThrownBy(() -> TokenSlidingWindow.create(0, 50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(TokenizerErrorMessage.MAX_TOKENS_POSITIVE.getMessage());
    }

    @Test
    @DisplayName("현재 메시지 목록은 외부에서 수정할 수 없다")
    void messagesAreImmutable() {
        TokenSlidingWindow window = TokenSlidingWindow.create(30, 50);
        ChatMessage message = new ChatMessage(Role.USER, "JVM이 뭐야?", 5);

        window.addMessage(message);

        assertThatThrownBy(() -> window.getMessages().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}