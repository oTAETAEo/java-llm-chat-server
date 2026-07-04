package com.example.aisocket.week3.aichat;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class SseParser {

    private static final int INITIAL_BYTE_CAPACITY = 8 * 1024;
    private static final int CHAR_CAPACITY = 4 * 1024;

    private final java.nio.charset.CharsetDecoder decoder =
            StandardCharsets.UTF_8.newDecoder();
    private final StringBuilder pendingText = new StringBuilder();
    private final List<String> dataLines = new ArrayList<>();

    private ByteBuffer pendingBytes = ByteBuffer.allocate(INITIAL_BYTE_CAPACITY);
    private String eventType;
    private String eventId;
    private Long retry;

    List<Event> accept(ByteBuffer source) {
        append(source);
        decode(false);
        return consumeCompleteLines(false);
    }

    List<Event> finish() {
        decode(true);
        List<Event> events = consumeCompleteLines(true);
        Event finalEvent = dispatch();
        if (finalEvent != null) {
            events.add(finalEvent);
        }
        return events;
    }

    private void append(ByteBuffer source) {
        ensureCapacity(source.remaining());
        pendingBytes.put(source);
    }

    private void ensureCapacity(int additionalBytes) {
        if (pendingBytes.remaining() >= additionalBytes) {
            return;
        }

        int required = pendingBytes.position() + additionalBytes;
        int capacity = pendingBytes.capacity();
        while (capacity < required) {
            capacity = Math.multiplyExact(capacity, 2);
        }

        ByteBuffer expanded = ByteBuffer.allocate(capacity);
        pendingBytes.flip();
        expanded.put(pendingBytes);
        pendingBytes = expanded;
    }

    private void decode(boolean endOfInput) {
        pendingBytes.flip();
        CharBuffer chars = CharBuffer.allocate(CHAR_CAPACITY);

        while (true) {
            CoderResult result = decoder.decode(pendingBytes, chars, endOfInput);
            chars.flip();
            pendingText.append(chars);
            chars.clear();

            if (result.isOverflow()) {
                continue;
            }
            if (result.isError()) {
                throw codingFailure(result);
            }
            break;
        }

        pendingBytes.compact();

        if (endOfInput) {
            while (true) {
                CoderResult result = decoder.flush(chars);
                chars.flip();
                pendingText.append(chars);
                chars.clear();
                if (result.isOverflow()) {
                    continue;
                }
                if (result.isError()) {
                    throw codingFailure(result);
                }
                break;
            }
        }
    }

    private IllegalArgumentException codingFailure(CoderResult result) {
        try {
            result.throwException();
            throw new AssertionError("unreachable");
        } catch (CharacterCodingException e) {
            return new IllegalArgumentException("Invalid UTF-8 in SSE stream", e);
        }
    }

    private List<Event> consumeCompleteLines(boolean includeTrailingLine) {
        List<Event> events = new ArrayList<>();
        int newline;

        while ((newline = pendingText.indexOf("\n")) >= 0) {
            String line = pendingText.substring(0, newline);
            pendingText.delete(0, newline + 1);
            processLine(stripCarriageReturn(line), events);
        }

        if (includeTrailingLine && !pendingText.isEmpty()) {
            String line = pendingText.toString();
            pendingText.setLength(0);
            processLine(stripCarriageReturn(line), events);
        }

        return events;
    }

    private void processLine(String line, List<Event> events) {
        if (line.isEmpty()) {
            Event event = dispatch();
            if (event != null) {
                events.add(event);
            }
            return;
        }
        if (line.startsWith(":")) {
            return;
        }

        int colon = line.indexOf(':');
        String field = colon < 0 ? line : line.substring(0, colon);
        String value = colon < 0 ? "" : line.substring(colon + 1);
        if (value.startsWith(" ")) {
            value = value.substring(1);
        }

        switch (field) {
            case "data" -> dataLines.add(value);
            case "event" -> eventType = value;
            case "id" -> {
                if (value.indexOf('\0') < 0) {
                    eventId = value;
                }
            }
            case "retry" -> {
                try {
                    retry = Long.parseLong(value);
                } catch (NumberFormatException ignored) {
                    // Invalid retry fields are ignored by the SSE specification.
                }
            }
            default -> {
                // Unknown fields are ignored by the SSE specification.
            }
        }
    }

    private Event dispatch() {
        if (dataLines.isEmpty()) {
            eventType = null;
            retry = null;
            return null;
        }

        Event event = new Event(
                String.join("\n", dataLines),
                eventType,
                eventId,
                retry
        );
        dataLines.clear();
        eventType = null;
        retry = null;
        return event;
    }

    private static String stripCarriageReturn(String line) {
        return line.endsWith("\r")
                ? line.substring(0, line.length() - 1)
                : line;
    }

    record Event(String data, String type, String id, Long retry) {

        byte[] toWireFormat() {
            StringBuilder wire = new StringBuilder();
            if (type != null) {
                wire.append("event: ").append(type).append('\n');
            }
            if (id != null) {
                wire.append("id: ").append(id).append('\n');
            }
            if (retry != null) {
                wire.append("retry: ").append(retry).append('\n');
            }
            for (String line : data.split("\n", -1)) {
                wire.append("data: ").append(line).append('\n');
            }
            wire.append('\n');
            return wire.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
}
