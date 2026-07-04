package com.example.aisocket.week3.aichat;

import java.io.IOException;
import java.net.URI;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiGatewayServer {

    private static final int PORT = 8080;
    private static final int SOCKET_BACKLOG = 1_024;
    private static final int MAX_CONCURRENT_AI_REQUESTS = 2_000;

    public static void main(String[] args) throws IOException {

        String apiKey = requiredEnvironmentVariable();
        URI aiEndpoint = URI.create("https://api.openai.com/v1/chat/completions");
        String model = "gpt-4o";

        // HttpClient 객체는 모든 가상 스레드에서 공유한다.
        HttpClient sharedHttpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();

        Semaphore capacity = new Semaphore(MAX_CONCURRENT_AI_REQUESTS);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
             ServerSocket serverSocket = new ServerSocket(PORT, SOCKET_BACKLOG)
        ) {
            while (true) {
                Socket userSocket = serverSocket.accept();
                executor.submit(() -> handleConnection(
                        userSocket,
                        sharedHttpClient,
                        aiEndpoint,
                        apiKey,
                        model,
                        capacity
                ));
            }
        }
    }

    private static void handleConnection(
            Socket socket,
            HttpClient httpClient,
            URI aiEndpoint,
            String apiKey,
            String model,
            Semaphore capacity
    ) {

        // AI 요청 스레드를 생성하기 전 2000개의 방 중 남은것이 있는지 확인
        if (!capacity.tryAcquire()) {
            try (socket) {
                HttpResponses.sendError(
                        socket.getOutputStream(),
                        503,
                        "AI gateway is at capacity"
                );
            } catch (IOException ignored) { }

            return;
        }

        try {
            new AsyncAiGatewayHandler(
                    socket,
                    httpClient,
                    aiEndpoint,
                    apiKey,
                    model
            ).run();
        } finally {
            capacity.release();
        }
    }

    private static String requiredEnvironmentVariable() {
        String value = System.getenv("OPENAI_API_KEY");

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY" + " 설정이 누락 되었습니다.");
        }

        return value;
    }

}
