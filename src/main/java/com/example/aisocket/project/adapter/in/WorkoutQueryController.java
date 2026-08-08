package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.response.FeedbackRoomWorkoutResponse;
import com.example.aisocket.project.adapter.in.security.AuthenticationMember;
import com.example.aisocket.project.application.in.WorkoutQueryService;
import com.example.aisocket.project.domain.WorkOutType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workouts/records")
@RequiredArgsConstructor
public class WorkoutQueryController {

    private final WorkoutQueryService workoutQueryService;

    @GetMapping("/{workOutType}/{workoutId}")
    public FeedbackRoomWorkoutResponse getWorkout(
            @AuthenticationMember Long memberId,
            @PathVariable WorkOutType workOutType,
            @PathVariable Long workoutId
    ) {
        return FeedbackRoomWorkoutResponse.from(
                workoutQueryService.getWorkout(memberId, workOutType, workoutId)
        );
    }
}
