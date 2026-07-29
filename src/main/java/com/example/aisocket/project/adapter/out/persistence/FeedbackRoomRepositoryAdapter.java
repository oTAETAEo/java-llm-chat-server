package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.FeedbackRoomRepository;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeedbackRoomRepositoryAdapter implements FeedbackRoomRepository {

    private final FeedbackRoomJpaRepository repository;

    @Override
    public FeedbackRoom save(FeedbackRoom room) {
        return repository.save(room);
    }

    @Override
    public FeedbackRoom create(Member member, String title) {
        return repository.save(FeedbackRoom.create(member, title));
    }

    @Override
    public Optional<FeedbackRoom> findByIdAndMemberId(UUID roomId, Long memberId) {
        return repository.findByIdAndMemberIdAndDeletedAtIsNull(roomId, memberId);
    }

    @Override
    public List<FeedbackRoom> findRecentByMemberId(Long memberId) {
        return repository.findTop20ByMemberIdAndPinnedFalseAndDeletedAtIsNullOrderByUpdatedAtDescCreatedAtDesc(memberId);
    }

    @Override
    public List<FeedbackRoom> findPinnedByMemberId(Long memberId) {
        return repository.findTop20ByMemberIdAndPinnedTrueAndDeletedAtIsNullOrderByUpdatedAtDescCreatedAtDesc(memberId);
    }
}
