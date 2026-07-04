package com.example.aisocket.week3.aichat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

record HttpRequestData(String method, String target, byte[] body) {

    static HttpRequestData read(
            InputStream input,
            int maxHeaderBytes,
            int maxBodyBytes
    ) throws IOException {
        byte[] headerBytes = readHeaders(input, maxHeaderBytes);
        String headerText = new String(headerBytes, StandardCharsets.ISO_8859_1);
        String[] lines = headerText.split("\\r?\\n");

        if (lines.length == 0) {
            throw new BadRequestException("Missing request line");
        }

        String[] requestLine = lines[0].split(" ", 3);
        if (requestLine.length != 3 || !requestLine[2].startsWith("HTTP/1.")) {
            throw new BadRequestException("Malformed request line");
        }

        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            int colon = lines[i].indexOf(':');
            if (colon <= 0) {
                throw new BadRequestException("Malformed request header");
            }
            headers.put(
                    lines[i].substring(0, colon).trim().toLowerCase(Locale.ROOT),
                    lines[i].substring(colon + 1).trim()
            );
        }

        int contentLength = parseContentLength(headers);
        if (contentLength > maxBodyBytes) {
            throw new BadRequestException("Request body is too large");
        }

        return new HttpRequestData(
                requestLine[0],
                requestLine[1],
                input.readNBytes(contentLength)
        ).requireCompleteBody(contentLength);
    }

    private HttpRequestData requireCompleteBody(int expectedLength) {
        if (body.length != expectedLength) {
            throw new BadRequestException("Incomplete request body");
        }
        return this;
    }

    private static byte[] readHeaders(InputStream input, int maxBytes)
            throws IOException {
        ByteArrayOutputStream headers = new ByteArrayOutputStream();
        int matched = 0;

        while (headers.size() < maxBytes) {
            int value = input.read();
            if (value < 0) {
                throw new BadRequestException("Connection closed before headers");
            }
            headers.write(value);

            matched = switch (matched) {
                case 0 -> value == '\r' ? 1 : 0;
                case 1 -> value == '\n' ? 2 : value == '\r' ? 1 : 0;
                case 2 -> value == '\r' ? 3 : 0;
                case 3 -> value == '\n' ? 4 : 0;
                default -> matched;
            };
            if (matched == 4) {
                byte[] all = headers.toByteArray();
                return java.util.Arrays.copyOf(all, all.length - 4);
            }
        }

        throw new BadRequestException("Request headers are too large");
    }

    private static int parseContentLength(Map<String, String> headers) {
        String rawLength = headers.get("content-length");
        if (rawLength == null) {
            throw new BadRequestException("Content-Length is required");
        }
        try {
            int length = Integer.parseInt(rawLength);
            if (length < 0) {
                throw new NumberFormatException();
            }
            return length;
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid Content-Length");
        }
    }

    static final class BadRequestException extends RuntimeException {
        BadRequestException(String message) {
            super(message);
        }
    }
}
