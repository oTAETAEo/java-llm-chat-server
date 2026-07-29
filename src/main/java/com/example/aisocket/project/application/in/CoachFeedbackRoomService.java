package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.result.FeedbackRoomDetailResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomSummaryResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;

import java.util.List;
import java.util.UUID;

public interface CoachFeedbackRoomService {

    FeedbackRoomSummaryResult createRoom(Long memberId);

    List<FeedbackRoomSummaryResult> findRecentRooms(Long memberId);

    List<FeedbackRoomSummaryResult> findPinnedRooms(Long memberId);

    FeedbackRoomSummaryResult renameRoom(Long memberId, UUID roomId, String title);

    FeedbackRoomSummaryResult pinRoom(Long memberId, UUID roomId);

    FeedbackRoomSummaryResult unpinRoom(Long memberId, UUID roomId);

    void deleteRoom(Long memberId, UUID roomId);

    FeedbackRoomDetailResult findRoom(Long memberId, UUID roomId);

    List<FeedbackRoomWorkoutResult> findRoomWorkouts(Long memberId, UUID roomId);

}
