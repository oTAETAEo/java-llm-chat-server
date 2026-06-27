package com.example.aisocket.week2.domain;

import lombok.Getter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class UserSession {

    @Getter
    private final String userId;

    private final Socket socket;

    @Getter
    private final BufferedReader reader;

    private final BufferedOutputStream writer;

    public UserSession(String userId, Socket socket) throws IOException {
        this.userId = userId;
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedOutputStream(socket.getOutputStream(), 8192);
    }

    public synchronized void send(String message) throws IOException {
        if (!socket.isClosed()) {
            writer.write(message.getBytes(StandardCharsets.UTF_8));
            writer.write("\n".getBytes(StandardCharsets.UTF_8));
            writer.flush();
        }
    }

    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {}

    }

}