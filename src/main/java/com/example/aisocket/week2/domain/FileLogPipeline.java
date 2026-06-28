package com.example.aisocket.week2.domain;

import java.util.concurrent.*;

public class FileLogPipeline implements AutoCloseable {

    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final LogRepository logRepository;

    private final ExecutorService executorService;

    public FileLogPipeline(LogRepository logRepository) {
        this.logRepository = logRepository;

        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("Log-Pipeline-Worker");
            return t;
        });

        startPipeline();
    }

    public void pushLog(String log) {
        logQueue.offer(log);
    }

    private void startPipeline() {

        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String logMessage = logQueue.take();

                    logRepository.write(logMessage);

                    if (logQueue.isEmpty()) {
                        logRepository.flush();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("[로그 파이프라인 엔진 예외 발생] : " + e.getMessage());
                }
            }
            logRepository.close();
        });
    }

    @Override
    public void close() {
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                System.err.println("[FileLogPipeline Warning] 강제 해제합니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}