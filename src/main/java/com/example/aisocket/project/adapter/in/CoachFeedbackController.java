package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.request.FeedbackRequest;
import com.example.aisocket.project.adapter.in.dto.request.FeedbackWithSensorRequest;
import com.example.aisocket.project.adapter.in.security.AuthenticationMember;
import com.example.aisocket.project.application.in.CoachFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CoachFeedbackController {

    private static final String FEEDBACK_STREAM_ERROR_MESSAGE = "피드백 생성 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.";

    private final CoachFeedbackService coachFeedbackService;

    @PostMapping(value = "/v1/coach/feedback/rooms/{roomId}/single/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateRoomSingleWorkoutFeedbackStream(
            @AuthenticationMember Long memberId,
            @PathVariable UUID roomId,
            @RequestBody FeedbackRequest request
    ) {

        SseEmitter emitter = new SseEmitter(0L);
        AtomicBoolean connected = new AtomicBoolean(true);

        Thread.startVirtualThread(() -> {
            try {
                coachFeedbackService.generateSingleWorkoutFeedbackStream(
                        memberId, roomId, request.toCommand(), chunk -> send(emitter, connected, memberId, roomId, chunk));
                completeIfConnected(emitter, connected);
            } catch (Exception e) {
                handleStreamFailure(emitter, connected, memberId, roomId, e);
            }
        });

        return emitter;
    }

    @PostMapping(value = "/v2/coach/feedback/rooms/{roomId}/single/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateRoomSingleWorkoutFeedbackStreamV2(
            @AuthenticationMember Long memberId,
            @PathVariable UUID roomId,
            @RequestBody FeedbackWithSensorRequest request
    ) {

        SseEmitter emitter = new SseEmitter(0L);
        AtomicBoolean connected = new AtomicBoolean(true);

        Thread.startVirtualThread(() -> {
            try {
                coachFeedbackService.generateSingleWorkoutFeedbackStream(
                        memberId, roomId, request.toCommand(), request.toSensorCommand(), chunk -> send(emitter, connected, memberId, roomId, chunk));
                completeIfConnected(emitter, connected);
            } catch (Exception e) {
                handleStreamFailure(emitter, connected, memberId, roomId, e);
            }
        });

        return emitter;
    }

    private void send(SseEmitter emitter, AtomicBoolean connected, Long memberId, UUID roomId, String chunk) {
        if (!connected.get()) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(new FeedbackStreamChunk(chunk), MediaType.APPLICATION_JSON));
        } catch (IOException | RuntimeException e) {
            connected.set(false);
            log.warn("피드백 SSE 연결이 종료되었습니다. AI 응답 생성과 저장은 계속합니다. memberId={}, roomId={}", memberId, roomId, e);
        }
    }

    private void completeIfConnected(SseEmitter emitter, AtomicBoolean connected) {
        if (!connected.get()) {
            return;
        }
        try {
            emitter.complete();
        } catch (RuntimeException e) {
            connected.set(false);
        }
    }

    private void handleStreamFailure(SseEmitter emitter, AtomicBoolean connected, Long memberId, UUID roomId, Exception exception) {
        log.error("피드백 스트림 생성에 실패했습니다. memberId={}, roomId={}", memberId, roomId, exception);
        if (!connected.get()) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(new FeedbackStreamError(FEEDBACK_STREAM_ERROR_MESSAGE), MediaType.APPLICATION_JSON));
            emitter.complete();
        } catch (IOException | RuntimeException sendFailure) {
            connected.set(false);
            log.warn("피드백 스트림 실패 이벤트 전송에 실패했습니다. memberId={}, roomId={}", memberId, roomId, sendFailure);
            try {
                emitter.completeWithError(exception);
            } catch (RuntimeException completeFailure) {
                log.debug("피드백 스트림 실패 완료 처리를 건너뜁니다. memberId={}, roomId={}", memberId, roomId, completeFailure);
            }
        }
    }

    private record FeedbackStreamChunk(String content) {
    }

    private record FeedbackStreamError(String message) {
    }

}
