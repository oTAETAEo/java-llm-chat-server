package com.example.aisocket.week2.adapter.in.socket;

import com.example.aisocket.week2.adapter.out.infra.AiClient;
import com.example.aisocket.week2.adapter.out.infra.OpenAIClient;
import com.example.aisocket.week2.adapter.out.persistence.DiskLogRepository;
import com.example.aisocket.week2.adapter.out.persistence.MemorySessionRepository;
import com.example.aisocket.week2.application.AIScheduler;
import com.example.aisocket.week2.domain.FileLogPipeline;
import com.example.aisocket.week2.domain.SessionRepository;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SocketApplication {

    private static final int SERVER_PORT = 8080;
    private static final Path LOG_FILE_PATH = Paths.get("./chat_async_history.log");

    public static void main(String[] args) {

        SessionRepository sessionRepository = new MemorySessionRepository();
        AiClient aiClient = new OpenAIClient();

        try (
                FileLogPipeline logPipeline = new FileLogPipeline(new DiskLogRepository(LOG_FILE_PATH));
                AIScheduler aiScheduler = new AIScheduler(aiClient)
        ) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logPipeline.close();
                aiScheduler.close();
                System.out.println("[Shutdown Hook] 버퍼 플러시 및 상주 일꾼 전원 퇴근 마감 완료.");
            }));

            logPipeline.pushLog("[System] 모든 비동기 완충 엔진 및 스케줄러 인프라 조립 완료.");
            serverStart(sessionRepository, aiScheduler, logPipeline);

        } catch (Exception e) {
            System.err.println("[Fatal] 서버 구동 중 치명적 예외 발생: " + e.getMessage());
        }
    }

    private static void serverStart(SessionRepository sessionRepository, AIScheduler aiScheduler, FileLogPipeline logPipeline) {
        ChatServerSocket serverSocket = new ChatServerSocket(SERVER_PORT, sessionRepository, aiScheduler, logPipeline);
        serverSocket.start();
    }

}
