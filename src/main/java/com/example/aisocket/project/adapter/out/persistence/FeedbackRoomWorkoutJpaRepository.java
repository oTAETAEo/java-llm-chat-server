package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackRoomWorkoutJpaRepository extends JpaRepository<FeedbackRoomWorkout, Long> {

    List<FeedbackRoomWorkout> findByRoomIdOrderByCreatedAtAscIdAsc(UUID roomId);
}
