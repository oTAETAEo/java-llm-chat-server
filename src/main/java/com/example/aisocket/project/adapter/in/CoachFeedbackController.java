package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.request.FeedbackRequest;
import com.example.aisocket.project.adapter.in.security.AuthenticationMember;
import com.example.aisocket.project.application.in.CoachFeedbackService;
import lombok.RequiredArgsConstructor;
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
public class CoachFeedbackController {

    private final CoachFeedbackService coachFeedbackService;


    @PostMapping(value = "/v1/coach/feedback/single/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateSingleWorkoutFeedbackStream(
            @AuthenticationMember Long memberId, @RequestBody FeedbackRequest request
    ) {

        SseEmitter emitter = new SseEmitter(0L);

        Thread.startVirtualThread(() -> {
            try {
                coachFeedbackService.getFeedbackStream(
                        memberId, request.toCommand(), chunk -> send(emitter, chunk));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

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
                emitter.completeWithError(e);
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

    private record FeedbackStreamChunk(String content) {
    }

}
