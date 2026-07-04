package com.example.aisocket.week3.aichat;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

final class HttpResponses {

    private HttpResponses() {
    }

    static void sendSseHeaders(OutputStream output) throws IOException {
        output.write("""
                HTTP/1.1 200 OK\r
                Content-Type: text/event-stream; charset=utf-8\r
                Cache-Control: no-cache\r
                Connection: close\r
                X-Accel-Buffering: no\r
                \r
                """.getBytes(StandardCharsets.US_ASCII));
        output.flush();
    }

    static void sendError(OutputStream output, int status, String message)
            throws IOException {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);
        String reason = switch (status) {
            case 400 -> "Bad Request";
            case 405 -> "Method Not Allowed";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            default -> "Error";
        };
        String headers = """
                HTTP/1.1 %d %s\r
                Content-Type: text/plain; charset=utf-8\r
                Content-Length: %d\r
                Connection: close\r
                \r
                """.formatted(status, reason, body.length);
        output.write(headers.getBytes(StandardCharsets.US_ASCII));
        output.write(body);
        output.flush();
    }
}
