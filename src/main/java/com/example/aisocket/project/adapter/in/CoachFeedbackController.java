package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.in.GetCoachFeedback;
import com.example.aisocket.project.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
public class CoachFeedbackController {

    private final GetCoachFeedback getCoachFeedback;

    @PostMapping("/v1/feedback")
    public ResponseEntity<String> generateFeedback(@RequestBody FeedbackRequest request) {

        Workout workout = request.toDomain();

        String feedback = getCoachFeedback.getFeedback(workout, request.tier());

        return ResponseEntity.ok(feedback);
    }

}