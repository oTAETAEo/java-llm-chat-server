package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.FeedbackRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackRoomJpaRepository extends JpaRepository<FeedbackRoom, UUID> {

    Optional<FeedbackRoom> findByIdAndMemberIdAndDeletedAtIsNull(UUID id, Long memberId);

    List<FeedbackRoom> findTop20ByMemberIdAndPinnedFalseAndDeletedAtIsNullOrderByUpdatedAtDescCreatedAtDesc(Long memberId);

    List<FeedbackRoom> findTop20ByMemberIdAndPinnedTrueAndDeletedAtIsNullOrderByUpdatedAtDescCreatedAtDesc(Long memberId);
}
