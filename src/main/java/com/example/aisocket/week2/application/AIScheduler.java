package com.example.aisocket.week2.application;

import com.example.aisocket.week2.adapter.out.infra.AiClient;
import com.example.aisocket.week2.domain.AIRequestTask;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class AIScheduler implements AutoCloseable {

    private final PriorityBlockingQueue<AIRequestTask> eventQueue = new PriorityBlockingQueue<>();
    private final AiClient aiClient;
    private final ExecutorService executorService;

    private static final int CONSUMER_THREAD_COUNT = 3;
    private static final int MAX_RETRIES = 5;
    private static final long BACKOFF_BASE_MS = 1000L;
    private static final long MAX_BACKOFF_MS = 16000L;

    public AIScheduler(AiClient aiClient) {
        this.aiClient = aiClient;
        // 스레드 풀 팩토리를 통해 명확한 이름 지명 및 고정 풀 생성
        this.executorService = Executors.newFixedThreadPool(CONSUMER_THREAD_COUNT, r -> {
            Thread t = new Thread(r);
            t.setName("AI-Scheduler-Consumer");
            return t;
        });
        startConsumerThreads();
    }

    public void enqueueTask(AIRequestTask task) {
        this.eventQueue.put(task);
    }

    private void startConsumerThreads() {
        for (int i = 0; i < CONSUMER_THREAD_COUNT; i++) {
            executorService.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        AIRequestTask task = eventQueue.take();
                        processWithExponentialBackoff(task);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("[AIScheduler 일꾼 예외 감지] : " + e.getMessage());
                    }
                }
            });
        }
    }

    private void processWithExponentialBackoff(AIRequestTask task) {
        boolean isSuccess = false;

        while (!isSuccess && task.getRetryCount() < MAX_RETRIES) {
            try {
                String response = aiClient.generateResponse(task.getPrompt());
                task.getResponseFuture().complete(response);
                isSuccess = true;
            } catch (Exception e) {
                task.incrementRetryCount();

                if (task.getRetryCount() >= MAX_RETRIES) {
                    task.getResponseFuture().completeExceptionally(
                            new RuntimeException("AI 서버의 응답 제한 제한 횟수를 초과했습니다. 다시 시도해 주세요.")
                    );
                    break;
                }

                long backoffTime = Math.min(MAX_BACKOFF_MS, BACKOFF_BASE_MS * (1L << task.getRetryCount()));
                long jitter = (long) (Math.random() * 300);
                long totalWaitTime = backoffTime + jitter;

                try {
                    Thread.sleep(totalWaitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    task.getResponseFuture().completeExceptionally(ie);
                    break;
                }
            }
        }
    }

    @Override
    public void close() {
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                System.err.println("[AIScheduler Warning] 강제 해제합니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}