package com.example.aisocket.week3.aichat;

import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

final class AsyncAiGatewayHandler implements Runnable {

    private static final int MAX_HEADER_BYTES = 16 * 1024;
    private static final int MAX_BODY_BYTES = 64 * 1024;

    private final Socket userSocket;
    private final HttpClient httpClient;
    private final URI aiEndpoint;
    private final String apiKey;
    private final String model;

    AsyncAiGatewayHandler(
            Socket userSocket,
            HttpClient httpClient,
            URI aiEndpoint,
            String apiKey,
            String model
    ) {
        this.userSocket = Objects.requireNonNull(userSocket);
        this.httpClient = Objects.requireNonNull(httpClient);
        this.aiEndpoint = Objects.requireNonNull(aiEndpoint);
        this.apiKey = Objects.requireNonNull(apiKey);
        this.model = Objects.requireNonNull(model);
    }

    @Override
    public void run() {
        try (Socket socket = userSocket;
             InputStream userIn = socket.getInputStream();
             OutputStream userOut = socket.getOutputStream()
        ) {

            // 30초 타임아웃
            socket.setSoTimeout(30_000);

            // InputStream으로 들어오는 바이트를 해석
//          POST / HTTP/1.1
//          Host: localhost:8080
//          Content-Type: application/json
//          Content-Length: 25
//
//          {"prompt":"안녕하세요"}
            HttpRequestData clientRequest =
                    HttpRequestData.read(userIn, MAX_HEADER_BYTES, MAX_BODY_BYTES);
            if (!"POST".equals(clientRequest.method())) {
                HttpResponses.sendError(userOut, 405, "Method Not Allowed");
                return;
            }

            // 사용자가 입력한 AI 프롬프트 추출
            String prompt = getUserCreatedPrompt(clientRequest);
            if (prompt == null || prompt.isBlank()) {
                HttpResponses.sendError(userOut, 400, "A non-empty prompt is required");
                return;
            }

            // 응답 전체를 메모리에 저장하는 대신 ByteBuffer 묶음을 스트리밍으로 전달
            HttpResponse<Flow.Publisher<List<ByteBuffer>>> aiResponse;
            try {
                // AI 요청 비동기로 실행
                aiResponse = httpClient.sendAsync(
                        createAiRequest(prompt),
                        HttpResponse.BodyHandlers.ofPublisher() // 응답 본문을 스트림 Publisher로 제공
                ).join();
            } catch (CompletionException e) {
                HttpResponses.sendError(userOut, 502, "AI request failed");
                return;
            }

            // AI 서버의 응답이 200 계열이 아니라면 스트리밍을 실행하지 않는다.
            if (aiResponse.statusCode() / 100 != 2) {
                HttpResponses.sendError(
                        userOut,
                        502,
                        "Upstream AI returned HTTP " + aiResponse.statusCode()
                );
                return;
            }

            // 사용자에게 SSE 응답 헤더 전달 (응답이 한 번에 끝나는 일반 응답이 아니라 SSE 형식으로 계속 전달된다는 사실 먼저 전달)
            HttpResponses.sendSseHeaders(userOut);

            // 사용자와 연결된 소켓 아웃 포트를 (AiStreamSubscriber 구독자) -> 생산자와 연결
            AiStreamSubscriber subscriber = new AiStreamSubscriber(userOut);
            aiResponse.body().subscribe(subscriber);

            try {
                subscriber.completion().join();
            } catch (CompletionException e) {
                System.err.println("[AI Stream Error] " + e.getCause());
            }

        } catch (HttpRequestData.BadRequestException e) {
            sendErrorIfPossible(400, e.getMessage());
        } catch (Exception e) {
            System.err.println("[AI Proxy Error] " + e.getMessage());
        }
    }

    @Nullable
    private String getUserCreatedPrompt(HttpRequestData clientRequest) {
        return JsonSupport.extractString(
                new String(clientRequest.body(), StandardCharsets.UTF_8),
                "prompt"
        );
    }

    private HttpRequest createAiRequest(String prompt) {
        String payload = """
                {
                  "model": "%s",
                  "messages": [{"role": "user", "content": "%s"}],
                  "stream": true
                }
                """.formatted(
                JsonSupport.escape(model),
                JsonSupport.escape(prompt)
        );

        return HttpRequest.newBuilder(aiEndpoint)
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
    }

    private void sendErrorIfPossible(int status, String message) {
        try {
            HttpResponses.sendError(userSocket.getOutputStream(), status, message);
        } catch (IOException ignored) {
            // The peer may already have disconnected.
        }
    }

    private static final class AiStreamSubscriber
            implements Flow.Subscriber<List<ByteBuffer>> {

        private final OutputStream userOut;
        private final SseParser parser = new SseParser();
        private final CompletableFuture<Void> completion = new CompletableFuture<>();
        private final BlockingQueue<Signal> signals = new ArrayBlockingQueue<>(2);

        private Flow.Subscription subscription;

        private AiStreamSubscriber(OutputStream userOut) {
            this.userOut = userOut;

            // 구독자 객체가 생성될 때 writeLoop 를 새로운 가상 스레드로 실행시킨다.
            Thread.startVirtualThread(this::writeLoop);
        }

        CompletableFuture<Void> completion() {
            return completion;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            if (this.subscription != null) {
                subscription.cancel();
                return;
            }
            this.subscription = subscription;
            subscription.request(1); // 첫 데이터 묶음 요청 (HttpClient Publisher에게 신호를 전달)
        }

        // 데이터 묶음이 도착 하면 HttpClient가 호출
        @Override
        public void onNext(List<ByteBuffer> buffers) {
            try {
                List<byte[]> events = new ArrayList<>();
                for (ByteBuffer buffer : buffers) {
                    for (SseParser.Event event : parser.accept(buffer)) {
                        events.add(event.toWireFormat());
                    }
                }
                enqueue(new DataSignal(events));
            } catch (Throwable error) {
                subscription.cancel();
                enqueue(new ErrorSignal(error));
            }
        }

        private void writeLoop() {
            try {
                // 상단 onNext 메서드를 통해 큐에 Signal 객체가 들어오면 소비한다.
                while (true) {
                    Signal signal = signals.take();
                    if (signal instanceof DataSignal data) {
                        for (byte[] event : data.events()) {
                            userOut.write(event);
                        }
                        userOut.flush();
                        subscription.request(1);
                    } else if (signal instanceof ErrorSignal error) {
                        throw new CompletionException(error.cause());
                    } else {
                        for (SseParser.Event event : parser.finish()) {
                            userOut.write(event.toWireFormat());
                        }
                        userOut.flush();
                        completion.complete(null);
                        return;
                    }
                }
            } catch (Throwable error) {
                if (subscription != null) {
                    subscription.cancel();
                }
                completion.completeExceptionally(error);
            }
        }

        @Override
        public void onError(Throwable error) {
            enqueue(new ErrorSignal(error));
        }

        @Override
        public void onComplete() {
            enqueue(CompleteSignal.INSTANCE);
        }

        private void enqueue(Signal signal) {
            if (!signals.offer(signal)) {
                if (subscription != null) {
                    subscription.cancel();
                }
                completion.completeExceptionally(
                        new IllegalStateException("Publisher violated bounded demand")
                );
            }
        }

        private sealed interface Signal
                permits DataSignal, ErrorSignal, CompleteSignal {
        }

        private record DataSignal(List<byte[]> events) implements Signal {
        }

        private record ErrorSignal(Throwable cause) implements Signal {
        }

        private enum CompleteSignal implements Signal {
            INSTANCE
        }
    }
}
