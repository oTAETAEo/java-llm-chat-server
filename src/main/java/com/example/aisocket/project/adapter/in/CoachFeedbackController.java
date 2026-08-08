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

        Thread.startVirtualThread(() -> {
            try {
                coachFeedbackService.generateSingleWorkoutFeedbackStream(
                        memberId, roomId, request.toCommand(), chunk -> send(emitter, chunk));
                emitter.complete();
            } catch (Exception e) {
                handleStreamFailure(emitter, memberId, roomId, e);
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

        Thread.startVirtualThread(() -> {
            try {
                coachFeedbackService.generateSingleWorkoutFeedbackStream(
                        memberId, roomId, request.toCommand(), request.toSensorCommand(), chunk -> send(emitter, chunk));
                emitter.complete();
            } catch (Exception e) {
                handleStreamFailure(emitter, memberId, roomId, e);
            }
        });

        return emitter;
    }

    private void send(SseEmitter emitter, String chunk) {
        try {
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(new FeedbackStreamChunk(chunk), MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            throw new IllegalStateException("SSE 응답 전송에 실패했습니다.", e);
        }
    }

    private void handleStreamFailure(SseEmitter emitter, Long memberId, UUID roomId, Exception exception) {
        log.error("피드백 스트림 생성에 실패했습니다. memberId={}, roomId={}", memberId, roomId, exception);
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(new FeedbackStreamError(FEEDBACK_STREAM_ERROR_MESSAGE), MediaType.APPLICATION_JSON));
            emitter.complete();
        } catch (IOException sendFailure) {
            log.warn("피드백 스트림 실패 이벤트 전송에 실패했습니다. memberId={}, roomId={}", memberId, roomId, sendFailure);
            emitter.completeWithError(exception);
        }
    }

    private record FeedbackStreamChunk(String content) {
    }

    private record FeedbackStreamError(String message) {
    }

}
