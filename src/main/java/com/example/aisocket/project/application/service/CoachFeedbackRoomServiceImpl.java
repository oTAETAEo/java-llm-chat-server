package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.result.FeedbackMessageResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomDetailResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomSummaryResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.in.CoachFeedbackRoomService;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.FeedbackMessageRepository;
import com.example.aisocket.project.application.out.FeedbackRoomRepository;
import com.example.aisocket.project.application.out.FeedbackRoomWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.WorkOutType;
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

    private final FeedbackRoomRepository feedbackRoomRepository;

    private final FeedbackMessageRepository feedbackMessageRepository;

    private final FeedbackRoomWorkoutRepository feedbackRoomWorkoutRepository;

    private final RunningWorkoutRepository runningWorkoutRepository;

    private final CyclingWorkoutRepository cyclingWorkoutRepository;

    @Override
    @Transactional
    public FeedbackRoomSummaryResult createRoom(Long memberId) {

        Member member = memberFinderService.findById(memberId);

        FeedbackRoom room = feedbackRoomRepository.create(member, "새 운동 피드백");

        return FeedbackRoomSummaryResult.from(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackRoomSummaryResult> findRecentRooms(Long memberId) {

        return feedbackRoomRepository.findRecentByMemberId(memberId).stream()
                .map(FeedbackRoomSummaryResult::from)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<FeedbackRoomSummaryResult> findPinnedRooms(Long memberId) {

        return feedbackRoomRepository.findPinnedByMemberId(memberId).stream()
                .map(FeedbackRoomSummaryResult::from)
                .toList();
    }

    @Override
    @Transactional
    public FeedbackRoomSummaryResult renameRoom(Long memberId, UUID roomId, String title) {

        FeedbackRoom room = findOwnedRoom(memberId, roomId);

        room.rename(title);

        return FeedbackRoomSummaryResult.from(feedbackRoomRepository.save(room));
    }

    @Override
    @Transactional
    public FeedbackRoomSummaryResult pinRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = findOwnedRoom(memberId, roomId);

        room.pin();

        return FeedbackRoomSummaryResult.from(feedbackRoomRepository.save(room));
    }

    @Override
    @Transactional
    public FeedbackRoomSummaryResult unpinRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = findOwnedRoom(memberId, roomId);

        room.unpin();

        return FeedbackRoomSummaryResult.from(feedbackRoomRepository.save(room));
    }

    @Override
    @Transactional
    public void deleteRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = findOwnedRoom(memberId, roomId);

        room.delete();

        feedbackRoomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackRoomDetailResult findRoom(Long memberId, UUID roomId) {

        FeedbackRoom room = findOwnedRoom(memberId, roomId);

        List<FeedbackMessageResult> messages = feedbackMessageRepository.findByRoomId(roomId).stream()
                .map(FeedbackMessageResult::from)
                .toList();
        return FeedbackRoomDetailResult.of(room, messages);
    }


    @Override
    @Transactional(readOnly = true)
    public List<FeedbackRoomWorkoutResult> findRoomWorkouts(Long memberId, UUID roomId) {

        findOwnedRoom(memberId, roomId);

        return feedbackRoomWorkoutRepository.findByRoomId(roomId).stream()
                .map(roomWorkout -> toWorkoutResult(memberId, roomWorkout))
                .toList();
    }

    private FeedbackRoomWorkoutResult toWorkoutResult(Long memberId, FeedbackRoomWorkout roomWorkout) {
        if (roomWorkout.getWorkoutType() == WorkOutType.RUNNING) {
            return runningWorkoutRepository.findByIdAndMemberId(roomWorkout.getWorkoutId(), memberId)
                    .map(FeedbackRoomWorkoutResult::from)
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
        }

        if (roomWorkout.getWorkoutType() == WorkOutType.CYCLING) {
            return cyclingWorkoutRepository.findByIdAndMemberId(roomWorkout.getWorkoutId(), memberId)
                    .map(FeedbackRoomWorkoutResult::from)
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
        }

        throw new ProjectException(WorkoutErrorCode.UNSUPPORTED_WORKOUT_TYPE);
    }

    private FeedbackRoom findOwnedRoom(Long memberId, UUID roomId) {
        return feedbackRoomRepository.findByIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new ProjectException(WorkoutErrorCode.FEEDBACK_ROOM_NOT_FOUND));
    }

}