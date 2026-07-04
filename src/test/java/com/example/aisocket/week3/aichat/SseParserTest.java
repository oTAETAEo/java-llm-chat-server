package com.example.aisocket.week3.aichat;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SseParserTest {

    @Test
    void parsesAnEventSplitAcrossArbitraryNetworkChunks() {
        SseParser parser = new SseParser();
        List<SseParser.Event> events = new ArrayList<>();

        events.addAll(parser.accept(bytes("dat")));
        events.addAll(parser.accept(bytes("a: {\"content\":\"안")));
        events.addAll(parser.accept(bytes("녕\"}\r")));
        events.addAll(parser.accept(bytes("\n\r\n")));

        assertThat(events)
                .extracting(SseParser.Event::data)
                .containsExactly("{\"content\":\"안녕\"}");
    }

    @Test
    void preservesUtf8CharactersSplitBetweenByteBuffers() {
        byte[] encoded = "data: 한글\n\n".getBytes(StandardCharsets.UTF_8);
        int splitInsideFirstKoreanCharacter =
                "data: ".getBytes(StandardCharsets.UTF_8).length + 1;

        SseParser parser = new SseParser();
        List<SseParser.Event> events = new ArrayList<>();
        events.addAll(parser.accept(ByteBuffer.wrap(
                encoded,
                0,
                splitInsideFirstKoreanCharacter
        )));
        events.addAll(parser.accept(ByteBuffer.wrap(
                encoded,
                splitInsideFirstKoreanCharacter,
                encoded.length - splitInsideFirstKoreanCharacter
        )));

        assertThat(events)
                .extracting(SseParser.Event::data)
                .containsExactly("한글");
    }

    @Test
    void parsesMultipleAndMultilineEventsFromOneChunk() {
        SseParser parser = new SseParser();

        List<SseParser.Event> events = parser.accept(bytes("""
                event: message
                id: 7
                data: first
                data: second

                data: [DONE]

                """));

        assertThat(events).hasSize(2);
        assertThat(events.get(0).data()).isEqualTo("first\nsecond");
        assertThat(events.get(0).type()).isEqualTo("message");
        assertThat(events.get(0).id()).isEqualTo("7");
        assertThat(events.get(1).data()).isEqualTo("[DONE]");
    }

    @Test
    void serializesParsedEventAsValidSse() {
        SseParser.Event event =
                new SseParser.Event("first\nsecond", "message", "9", 1_000L);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.writeBytes(event.toWireFormat());

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo("""
                event: message
                id: 9
                retry: 1000
                data: first
                data: second

                """);
    }

    private static ByteBuffer bytes(String value) {
        return ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8));
    }
}
