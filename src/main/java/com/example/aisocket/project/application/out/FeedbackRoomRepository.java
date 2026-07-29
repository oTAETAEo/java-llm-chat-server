package com.example.aisocket.project.application.out;

import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackRoomRepository {

    FeedbackRoom save(FeedbackRoom room);

    FeedbackRoom create(Member member, String title);

    Optional<FeedbackRoom> findByIdAndMemberId(UUID roomId, Long memberId);

    List<FeedbackRoom> findRecentByMemberId(Long memberId);

    List<FeedbackRoom> findPinnedByMemberId(Long memberId);
}
