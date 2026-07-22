package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.request.FeedbackRequest;
import com.example.aisocket.project.application.in.CoachFeedbackService;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/coach/feedback")
@RequiredArgsConstructor
public class CoachFeedbackController {

    private final CoachFeedbackService coachFeedbackService;

    @PostMapping(value = "/single/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateSingleWorkoutFeedbackStream(@RequestBody FeedbackRequest request) {

        // TODO : 스프링 시큐리티 적용 시 제거
        Member member = Member.of(1L, null, null, "temporary-user");
        SseEmitter emitter = new SseEmitter(0L);
        Thread.startVirtualThread(() -> {
            try {
                coachFeedbackService.getFeedbackStream(
                        member, request.toCommand(), chunk -> send(emitter, chunk));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private void send(SseEmitter emitter, String chunk) {
        try {
            emitter.send(SseEmitter.event().data(chunk));
        } catch (IOException e) {
            throw new IllegalStateException("SSE 응답 전송에 실패했습니다.", e);
        }
    }

}
