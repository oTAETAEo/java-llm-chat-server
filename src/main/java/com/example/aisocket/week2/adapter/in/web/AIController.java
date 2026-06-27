package com.example.aisocket.week2.adapter.in.web;

import com.example.aisocket.week2.application.AIScheduler;
import com.example.aisocket.week2.domain.AIRequestTask;
import com.example.aisocket.week2.domain.UserGrade;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v2/ai")
public class AIController {

    private final AIScheduler aiScheduler;

    public AIController(AIScheduler aiScheduler) {
        this.aiScheduler = aiScheduler;
    }

    @PostMapping("/ask")
    public DeferredResult<String> askAI(
            @RequestParam String prompt,
            @RequestParam(defaultValue = "LOW") UserGrade userGrade
    ) {

        // 1. 톰캣 엔진과 스프링 관제탑이 공유할 비동기 응답 가방(DeferredResult) 생성 (30초 타임아웃)
        DeferredResult<String> deferredResult = new DeferredResult<>(30000L);

        // 2. 백그라운드 소비 스레드와 바톤 터치를 할 자바 표준 CompletableFuture 가방 생성
        CompletableFuture<String> responseFuture = new CompletableFuture<>();

        // 3. 도메인 레이어의 일감 세트(AIRequestTask) 조립
        AIRequestTask task = new AIRequestTask(prompt, userGrade, responseFuture, UUID.randomUUID().toString());

        // 4. 스케줄러 내부의 PriorityBlockingQueue 필드에 일감 주입
        aiScheduler.enqueueTask(task);

        // 5. CompletableFuture에 값이 채워지면 DeferredResult에도 자동으로 동킹되도록 흐름 연결
        // 이 콜백은 나중에 백그라운드 스레드가 complete()를 치는 순간 터집니다.
        responseFuture.thenAccept(deferredResult::setResult)
                .exceptionally(throwable -> {
                    deferredResult.setErrorResult(throwable.getMessage());
                    return null;
                });

        // 6. 소비 스레드를 절대로 동기식으로 기다리지 않고, 가방만 톰캣 엔진에 리턴하며 exec-X 즉시 칼퇴근!
        return deferredResult;
    }
}