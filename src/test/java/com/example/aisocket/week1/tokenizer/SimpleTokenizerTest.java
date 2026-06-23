package com.example.aisocket.week1.tokenizer;

import com.example.aisocket.week1.tokenizer.SimpleTokenizer;
import com.example.aisocket.week1.tokenizer.Tokenizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTokenizerTest {

    private final Tokenizer tokenizer = new SimpleTokenizer();

    @Test
    @DisplayName("null 또는 공백 문자열은 0토큰으로 계산한다")
    void blankTextReturnsZero() {
        assertThat(tokenizer.countTokens(null)).isZero();
        assertThat(tokenizer.countTokens("")).isZero();
        assertThat(tokenizer.countTokens("   ")).isZero();
        assertThat(tokenizer.countTokens("\n\t")).isZero();
    }

    @Test
    @DisplayName("한글은 한 글자당 1토큰으로 계산한다")
    void countKoreanTokens() {
        assertThat(tokenizer.countTokens("안녕")).isEqualTo(2);
        assertThat(tokenizer.countTokens("뭐야")).isEqualTo(2);
        assertThat(tokenizer.countTokens("자바")).isEqualTo(2);
    }

    @Test
    @DisplayName("영어와 숫자는 4글자당 1토큰으로 계산한다")
    void countAlphaNumericTokens() {
        assertThat(tokenizer.countTokens("Java")).isEqualTo(1);
        assertThat(tokenizer.countTokens("Spring")).isEqualTo(2);
        assertThat(tokenizer.countTokens("SpringBoot")).isEqualTo(3);
        assertThat(tokenizer.countTokens("Java21")).isEqualTo(2);
        assertThat(tokenizer.countTokens("GPT4")).isEqualTo(1);
    }

    @Test
    @DisplayName("공백은 토큰으로 계산하지 않고 영어/숫자 구간을 분리한다")
    void whitespaceSeparatesAlphaNumericTokens() {
        assertThat(tokenizer.countTokens("Java Spring")).isEqualTo(3);
        assertThat(tokenizer.countTokens("AI Model")).isEqualTo(3);
    }

    @Test
    @DisplayName("특수문자는 1개당 1토큰으로 계산한다")
    void countSpecialCharacters() {
        assertThat(tokenizer.countTokens("?")).isEqualTo(1);
        assertThat(tokenizer.countTokens("!")).isEqualTo(1);
        assertThat(tokenizer.countTokens("Hello, Java!")).isEqualTo(5);
    }

    @Test
    @DisplayName("한글과 영어가 섞인 문장을 계산한다")
    void countMixedKoreanAndEnglishTokens() {
        assertThat(tokenizer.countTokens("JVM이 뭐야?")).isEqualTo(5);
        assertThat(tokenizer.countTokens("AI모델")).isEqualTo(3);
        assertThat(tokenizer.countTokens("SpringBoot는 Java21에서 사용해")).isEqualTo(11);
    }

    @Test
    @DisplayName("영어 뒤에 한글이 바로 붙어도 영어 구간을 먼저 정산한다")
    void flushAlphaNumericWhenKoreanAppears() {
        assertThat(tokenizer.countTokens("Java개발자")).isEqualTo(4);
        assertThat(tokenizer.countTokens("GPT모델")).isEqualTo(3);
    }

    @Test
    @DisplayName("영어 뒤에 특수문자가 나오면 영어 구간을 먼저 정산한다")
    void flushAlphaNumericWhenSpecialCharacterAppears() {
        assertThat(tokenizer.countTokens("JVM?")).isEqualTo(2);
        assertThat(tokenizer.countTokens("Java!")).isEqualTo(2);
        assertThat(tokenizer.countTokens("SpringBoot!")).isEqualTo(4);
    }
}