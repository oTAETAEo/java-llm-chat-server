package com.example.aisocket.week2.adapter.out.persistence;

import com.example.aisocket.week2.domain.LogRepository;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DiskLogRepository implements LogRepository {

    private final BufferedOutputStream bos;

    public DiskLogRepository(String logFilePath) throws IOException {
        this.bos = new BufferedOutputStream(new FileOutputStream(logFilePath, true), 8192);
    }

    @Override
    public void write(String logMessage) throws IOException {
        bos.write(logMessage.getBytes(StandardCharsets.UTF_8));
        bos.write("\n".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void flush() throws IOException {
        bos.flush();
    }

    @Override
    public void close() {
        try {
            bos.flush();
            bos.close();
        } catch (IOException ignored) {}
    }
}