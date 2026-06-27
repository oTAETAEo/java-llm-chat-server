package com.example.aisocket.week2.domain;

import lombok.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileLogPipeline {

    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final LogRepository logRepository;
    private Thread workerThread;

    public FileLogPipeline(LogRepository logRepository) {
        this.logRepository = logRepository;
        startPipeline();
    }

    public void pushLog(String log) {
        logQueue.offer(log);
    }

    private void startPipeline() {
        this.workerThread = createPipelineThread();
        this.workerThread.start();
    }

    private @NonNull Thread createPipelineThread() {
        return new Thread(() -> {
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
                    System.err.println("[로그 파이프라인 엔진 에러] : " + e.getMessage());
                }
            }

            logRepository.close();
        }, "Log-Pipeline-Worker");
    }

    public void stop() {
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }
}