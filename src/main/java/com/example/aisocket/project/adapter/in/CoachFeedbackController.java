package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.in.CoachFeedback;
import com.example.aisocket.project.domain.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
public class CoachFeedbackController {

    private final CoachFeedback coachFeedback;

    private final WorkoutMapper workoutMapper;

    @PostMapping("/v1/feedback")
    public Mono<String> generateFeedback(@RequestBody FeedbackRequest request) {
        Workout workout = workoutMapper.toWorkout(request);

        return coachFeedback.getFeedback(workout, request.tier());
    }

    @PostMapping(value = "/v2/feedback", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateFeedbackStream(@RequestBody FeedbackRequest request) {
        Workout workout = workoutMapper.toWorkout(request);

        return coachFeedback.getFeedbackStream(workout, request.tier());
    }

}
