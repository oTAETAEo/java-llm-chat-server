package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.in.CoachFeedback;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
public class CoachFeedbackController {

    private final CoachFeedback coachFeedback;

    private final WorkoutMapper workoutMapper;

    @PostMapping("/v1/feedback")
    public String generateFeedback(@RequestBody FeedbackRequest request) {
        Workout workout = workoutMapper.toWorkout(request);

        return coachFeedback.getFeedback(workout, request.tier());
    }

    @PostMapping(value = "/v2/feedback", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateFeedbackStream(@RequestBody FeedbackRequest request) {

        Workout workout = workoutMapper.toWorkout(request);

        SseEmitter emitter = new SseEmitter(0L);

        Thread.startVirtualThread(() -> {
            try {
                coachFeedback.getFeedbackStream(
                        workout, request.tier(), chunk -> send(emitter, chunk));
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
