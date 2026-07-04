package com.example.aisocket.week3.aichat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSupportTest {

    @Test
    void escapesUserTextForAnAiRequest() {
        assertThat(JsonSupport.escape("말 \"하나\"\n경로\\파일"))
                .isEqualTo("말 \\\"하나\\\"\\n경로\\\\파일");
    }

    @Test
    void extractsPromptIncludingJsonEscapes() {
        String json = """
                {"metadata":{"id":1},"prompt":"안녕 \\"AI\\"\\n다음 줄"}
                """;

        assertThat(JsonSupport.extractString(json, "prompt"))
                .isEqualTo("안녕 \"AI\"\n다음 줄");
    }

    @Test
    void returnsNullForMalformedOrMissingPrompt() {
        assertThat(JsonSupport.extractString("not-json", "prompt")).isNull();
        assertThat(JsonSupport.extractString("{\"other\":\"value\"}", "prompt"))
                .isNull();
    }
}
