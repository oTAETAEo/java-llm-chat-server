package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.request.UpdateFeedbackRoomTitleRequest;
import com.example.aisocket.project.adapter.in.dto.response.CreateFeedbackRoomResponse;
import com.example.aisocket.project.adapter.in.dto.response.FeedbackRoomDetailResponse;
import com.example.aisocket.project.adapter.in.dto.response.FeedbackRoomSummaryResponse;
import com.example.aisocket.project.adapter.in.dto.response.FeedbackRoomWorkoutResponse;
import com.example.aisocket.project.adapter.in.security.AuthenticationMember;
import com.example.aisocket.project.application.in.CoachFeedbackRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coach/feedback/rooms")
@RequiredArgsConstructor
public class CoachFeedbackRoomController {

    private final CoachFeedbackRoomService coachFeedbackRoomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateFeedbackRoomResponse createRoom(@AuthenticationMember Long memberId) {

        return CreateFeedbackRoomResponse.from(coachFeedbackRoomService.createRoom(memberId));
    }

    @GetMapping("/recent")
    public List<FeedbackRoomSummaryResponse> findRecentRooms(@AuthenticationMember Long memberId) {

        return coachFeedbackRoomService.findRecentRooms(memberId).stream()
                .map(FeedbackRoomSummaryResponse::from)
                .toList();
    }

    @GetMapping("/pinned")
    public List<FeedbackRoomSummaryResponse> findPinnedRooms(@AuthenticationMember Long memberId) {

        return coachFeedbackRoomService.findPinnedRooms(memberId).stream()
                .map(FeedbackRoomSummaryResponse::from)
                .toList();
    }

    @PatchMapping("/{roomId}/title")
    public FeedbackRoomSummaryResponse renameRoom(
            @AuthenticationMember Long memberId, @PathVariable UUID roomId,
            @RequestBody UpdateFeedbackRoomTitleRequest request) {

        return FeedbackRoomSummaryResponse.from(
                coachFeedbackRoomService.renameRoom(memberId, roomId, request.title()));
    }

    @PatchMapping("/{roomId}/pin")
    public FeedbackRoomSummaryResponse pinRoom(
            @AuthenticationMember Long memberId, @PathVariable UUID roomId) {

        return FeedbackRoomSummaryResponse.from(
                coachFeedbackRoomService.pinRoom(memberId, roomId));
    }

    @PatchMapping("/{roomId}/unpin")
    public FeedbackRoomSummaryResponse unpinRoom(
            @AuthenticationMember Long memberId,
            @PathVariable UUID roomId) {

        return FeedbackRoomSummaryResponse.from(
                coachFeedbackRoomService.unpinRoom(memberId, roomId));
    }

    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(
            @AuthenticationMember Long memberId, @PathVariable UUID roomId) {

        coachFeedbackRoomService.deleteRoom(memberId, roomId);
    }

    @GetMapping("/{roomId}")
    public FeedbackRoomDetailResponse findRoom(
            @AuthenticationMember Long memberId, @PathVariable UUID roomId) {

        return FeedbackRoomDetailResponse.from(coachFeedbackRoomService.findRoom(memberId, roomId));
    }

    @GetMapping("/{roomId}/workouts")
    public List<FeedbackRoomWorkoutResponse> findRoomWorkouts(
            @AuthenticationMember Long memberId,
            @PathVariable UUID roomId) {

        return coachFeedbackRoomService.findRoomWorkouts(memberId, roomId).stream()
                .map(FeedbackRoomWorkoutResponse::from)
                .toList();
    }

}