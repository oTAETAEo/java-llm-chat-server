package com.example.aisocket.week2.application;

import com.example.aisocket.week2.adapter.out.infra.AiClient;
import com.example.aisocket.week2.domain.AIRequestTask;
import org.springframework.stereotype.Component;

import java.util.concurrent.PriorityBlockingQueue;

@Component
public class AIScheduler {

    private final PriorityBlockingQueue<AIRequestTask> eventQueue = new PriorityBlockingQueue<>();
    private final AiClient aiClient;

    private static final int CONSUMER_THREAD_COUNT = 3;
    private static final int MAX_RETRIES = 5;
    private static final long BACKOFF_BASE_MS = 1000L;
    private static final long MAX_BACKOFF_MS = 16000L;

    public AIScheduler(AiClient aiClient) {
        this.aiClient = aiClient;
        startConsumerThreads();
    }

    /**
     * 인바운드 어댑터(Controller)가 호출할 일감 주입 대문
     */
    public void enqueueTask(AIRequestTask task) {
        this.eventQueue.put(task);
    }

    /**
     * 톰캣 풀과 완전히 격리되어 루프를 돌 전담 일꾼 스레드 가동
     */
    private void startConsumerThreads() {
        for (int i = 0; i < CONSUMER_THREAD_COUNT; i++) {
            Thread consumerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 큐의 맨 앞에서 가중치 순으로 일감을 하나씩 꺼낸다 (일감이 없으면 CPU 자원을 쓰지 않고 차단 대기)
                        AIRequestTask task = eventQueue.take();

                        // 핵심 지수 백오프 알고리즘 작동
                        processWithExponentialBackoff(task);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "AI-Scheduler-Consumer-" + i);

            consumerThread.start();
        }
    }

    /**
     * 지수 백오프(Exponential Backoff) + 지터(Jitter) 기반 로직
     */
    private void processWithExponentialBackoff(AIRequestTask task) {
        boolean isSuccess = false;

        while (!isSuccess && task.getRetryCount() < MAX_RETRIES) {
            try {
                // 아웃바운드 포트(AiClient)를 통해 실제 외부 AI API 타격
                String response = aiClient.generateResponse(task.getPrompt());

                task.getResponseFuture().complete(response);
                isSuccess = true;

            } catch (Exception e) {
                task.incrementRetryCount();

                if (task.getRetryCount() >= MAX_RETRIES) {
                    // 최대 재시도 실패 시 가방에 에러를 담아 유저에게 즉시 예외 반환
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
}