package com.example.aisocket.week2.adapter.out.persistence;

import com.example.aisocket.week2.domain.LogRepository;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class DiskLogRepository implements LogRepository {

    private final BufferedOutputStream bos;

    public DiskLogRepository(Path filePath) throws IOException {
        this.bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile(), true));
    }

    @Override
    public void write(String logMessage) throws IOException {
        String timedLog = "[" + java.time.LocalDateTime.now() + "] " + logMessage + "\n";
        bos.write(timedLog.getBytes(StandardCharsets.UTF_8));
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