package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.result.FeedbackMessageResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomDetailResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomSummaryResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.in.CoachFeedbackRoomService;
import com.example.aisocket.project.application.internal.feedback.FeedbackRoomRecordService;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.internal.workout.FeedbackRoomWorkoutQueryService;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class CoachFeedbackRoomServiceImpl implements CoachFeedbackRoomService {

    private final MemberFinderService memberFinderService;

    private final FeedbackRoomRecordService feedbackRoomRecordService;

    private final FeedbackRoomWorkoutQueryService feedbackRoomWorkoutQueryService;

    @Override
    @Transactional
    public FeedbackRoomSummaryResult createRoom(Long memberId) {

        Member member = memberFinderService.findById(memberId);

        FeedbackRoom room = feedbackRoomRecordService.createRoom(member, "새 운동 피드백");

        return FeedbackRoomSummaryResult.from(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackRoomSummaryResult> findRecentRooms(Long memberId) {

        return feedbackRoomRecordService.findRecentRooms(memberId).stream()
                .map(FeedbackRoomSummaryResult::from)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<FeedbackRoomSummaryResult> findPinnedRooms(Long memberId) {

        return feedbackRoomRecordService.findPinnedRooms(memberId).stream()
                .map(FeedbackRoomSummaryResult::from)
                .toList();
    }

    @Override
    @Transactional
    public FeedbackRoomSummaryResult renameRoom(Long memberId, UUID roomId, String title) {

        FeedbackRoom room = feedbackRoomRecordService.findOwnedRoom(memberId, roomId);

        room.rename(title);

        return FeedbackRoomSummaryResult.from(feedbackRoomRecordService.saveRoom(room));
    }

    @Override
    @Transactional
    public FeedbackRoomSummaryResult pinRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = feedbackRoomRecordService.findOwnedRoom(memberId, roomId);

        room.pin();

        return FeedbackRoomSummaryResult.from(feedbackRoomRecordService.saveRoom(room));
    }

    @Override
    @Transactional
    public FeedbackRoomSummaryResult unpinRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = feedbackRoomRecordService.findOwnedRoom(memberId, roomId);

        room.unpin();

        return FeedbackRoomSummaryResult.from(feedbackRoomRecordService.saveRoom(room));
    }

    @Override
    @Transactional
    public void deleteRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = feedbackRoomRecordService.findOwnedRoom(memberId, roomId);

        room.delete();

        feedbackRoomRecordService.saveRoom(room);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackRoomDetailResult findRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = feedbackRoomRecordService.findOwnedRoom(memberId, roomId);

        List<FeedbackMessageResult> messages = feedbackRoomRecordService.findMessages(roomId).stream()
                .map(FeedbackMessageResult::from)
                .toList();
        return FeedbackRoomDetailResult.of(room, messages);
    }


    @Override
    @Transactional(readOnly = true)
    public List<FeedbackRoomWorkoutResult> findRoomWorkouts(Long memberId, UUID roomId) {

        feedbackRoomRecordService.findOwnedRoom(memberId, roomId);

        return feedbackRoomRecordService.findRoomWorkouts(roomId).stream()
                .map(roomWorkout -> feedbackRoomWorkoutQueryService.findWorkout(memberId, roomWorkout))
                .toList();
    }

}
