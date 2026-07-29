package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.FeedbackMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackMessageJpaRepository extends JpaRepository<FeedbackMessage, Long> {

    List<FeedbackMessage> findByRoomIdOrderByCreatedAtAscIdAsc(UUID roomId);
}
