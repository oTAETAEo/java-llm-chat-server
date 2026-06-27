package com.example.aisocket.week2.adapter.in.socket;

import com.example.aisocket.week2.application.AIScheduler;
import com.example.aisocket.week2.domain.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatServerSocket {

    private final int port;
    private final SessionRepository sessionRepository;
    private final AIScheduler aiScheduler;
    private final FileLogPipeline logPipeline;

    public ChatServerSocket(int port,
                            SessionRepository sessionRepository,
                            AIScheduler aiScheduler,
                            FileLogPipeline logPipeline) {
        this.port = port;
        this.sessionRepository = sessionRepository;
        this.aiScheduler = aiScheduler;
        this.logPipeline = logPipeline;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 [소켓 서버 가동] 포트 번호: " + port + "에서 연결을 대기합니다.");

            while (!Thread.currentThread().isInterrupted()) {
                // 외부 클라이언트가 접속할 때까지 차단 대기 (Accept)
                Socket clientSocket = serverSocket.accept();

                // 톰캣 스레드 풀을 방어하기 위해 가상 스레드(Virtual Thread)로 유저별 전담 마크 일꾼 배정!
                Thread.startVirtualThread(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("[서버 소켓 에러] : " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {

        String userId = "User-" + UUID.randomUUID().toString().substring(0, 5);
        String roomId = UUID.randomUUID().toString();
        UserSession session = null;

        try {
            // 1. 소켓 입출구 스트림을 묶어 세션 객체 생성
            session = new UserSession(userId, clientSocket);

            // 2. 도메인 인터페이스를 통해 영속성 주소록 어댑터에 유저 등록
            sessionRepository.save(roomId, session);

            session.send("[접속 성공] 환영합니다, " + userId + "님! (방 ID: " + roomId + ")");
            logPipeline.pushLog("[입장] " + userId + "님이 방 [" + roomId + "]에 입장했습니다.");


            // 3. 유저가 타이핑을 쳐서 엔터를 누를 때마다 한 줄씩 수집하는 루프
            BufferedReader reader = session.getReader();
            String message;

            while ((message = reader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(message.trim())) {
                    break;
                }

                // 무손실 파일 로그 파이프라인 엔진 큐에 0.0001초 만에 토스
                logPipeline.pushLog("[" + roomId + "] " + userId + ": " + message);

                // AI 응답을 비동기로 받아낼 주머니(Future) 준비
                CompletableFuture<String> responseFuture = new CompletableFuture<>();

                // 태현님이 확장하신 생성자 포맷 그대로 일감 메모지 생성!
                AIRequestTask task = new AIRequestTask(message, UserGrade.HIGH, responseFuture, roomId);

                // 4. 애플리케이션 지휘자(AIScheduler)의 비동기 백오프 큐에 일감 위임!
                aiScheduler.enqueueTask(task);

                // 5. [비동기 콜백 체인] AI 답변이 완료되는 시점에 호출될 액션 정의
                responseFuture.thenAccept(aiResponse -> {
                    sessionRepository.findByRoomId(roomId).forEach(userSession -> {
                        try {
                            userSession.send("[AI 응답]: " + aiResponse);
                        } catch (IOException e) {
                            System.err.println("[브로드캐스트 실패] : " + e.getMessage());
                        }
                    });
                    logPipeline.pushLog("[" + roomId + "] AI: " + aiResponse);
                });
            }

        } catch (IOException e) {
            System.err.println("[" + userId + " 통신 끊김] : " + e.getMessage());
        } finally {
            // 6. 유저 퇴장 시 장부 마감 및 자원 청소
            if (session != null) {
                sessionRepository.remove(roomId, session);
                logPipeline.pushLog("[퇴장] " + userId + "님이 방 [" + roomId + "]에서 퇴장했습니다.");
            }
        }
    }
}