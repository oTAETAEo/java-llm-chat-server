package com.example.aisocket.week3.aichat;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestDataTest {

    @Test
    void contentLengthIsMeasuredInBytesNotCharacters() throws Exception {
        byte[] body = "{\"prompt\":\"안녕\"}".getBytes(StandardCharsets.UTF_8);
        byte[] request = (
                "POST / HTTP/1.1\r\n"
                        + "Content-Length: " + body.length + "\r\n"
                        + "Content-Type: application/json\r\n\r\n"
        ).getBytes(StandardCharsets.US_ASCII);
        byte[] all = new byte[request.length + body.length];
        System.arraycopy(request, 0, all, 0, request.length);
        System.arraycopy(body, 0, all, request.length, body.length);

        HttpRequestData parsed = HttpRequestData.read(
                new ByteArrayInputStream(all),
                16 * 1024,
                64 * 1024
        );

        assertThat(parsed.method()).isEqualTo("POST");
        assertThat(parsed.body()).isEqualTo(body);
    }

    @Test
    void rejectsOversizedBodiesBeforeAllocatingThem() {
        String request = """
                POST / HTTP/1.1\r
                Content-Length: 1000\r
                \r
                """;

        assertThatThrownBy(() -> HttpRequestData.read(
                new ByteArrayInputStream(
                        request.getBytes(StandardCharsets.US_ASCII)
                ),
                1024,
                10
        )).isInstanceOf(HttpRequestData.BadRequestException.class)
                .hasMessageContaining("too large");
    }

    @Test
    void rejectsAnIncompleteBody() {
        String request = """
                POST / HTTP/1.1\r
                Content-Length: 10\r
                \r
                short
                """;

        assertThatThrownBy(() -> HttpRequestData.read(
                new ByteArrayInputStream(
                        request.getBytes(StandardCharsets.US_ASCII)
                ),
                1024,
                1024
        )).isInstanceOf(HttpRequestData.BadRequestException.class)
                .hasMessageContaining("Incomplete");
    }
}
