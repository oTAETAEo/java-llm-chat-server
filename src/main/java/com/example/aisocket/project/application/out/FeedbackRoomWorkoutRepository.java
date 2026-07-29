package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.FeedbackRoomWorkout;

import java.util.List;
import java.util.UUID;

public interface FeedbackRoomWorkoutRepository {

    FeedbackRoomWorkout save(FeedbackRoomWorkout roomWorkout);

    List<FeedbackRoomWorkout> findByRoomId(UUID roomId);
}
