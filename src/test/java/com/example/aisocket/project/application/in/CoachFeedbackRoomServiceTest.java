package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.result.FeedbackRoomDetailResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomSummaryResult;
import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.FeedbackMessageRepository;
import com.example.aisocket.project.application.out.FeedbackRoomRepository;
import com.example.aisocket.project.application.out.FeedbackRoomWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
import com.example.aisocket.project.domain.FeedbackMessage;
import com.example.aisocket.project.domain.FeedbackMessageFixture;
import com.example.aisocket.project.domain.FeedbackMessageRole;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.FeedbackRoomFixture;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import com.example.aisocket.project.domain.FeedbackRoomWorkoutFixture;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.times;

class CoachFeedbackRoomServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private CoachFeedbackRoomService coachFeedbackRoomService;

    @MockitoBean
    private MemberFinderService memberFinderService;

    @MockitoBean
    private FeedbackRoomRepository feedbackRoomRepository;

    @MockitoBean
    private FeedbackMessageRepository feedbackMessageRepository;

    @MockitoBean
    private FeedbackRoomWorkoutRepository feedbackRoomWorkoutRepository;

    @MockitoBean
    private RunningWorkoutRepository runningWorkoutRepository;

    @MockitoBean
    private CyclingWorkoutRepository cyclingWorkoutRepository;

    @Test
    @DisplayName("피드백 방을 생성한다")
    void createRoom() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder()
                .member(member)
                .title("새 운동 피드백")
                .build();
        given(memberFinderService.findById(member.getId())).willReturn(member);
        given(feedbackRoomRepository.create(member, "새 운동 피드백")).willReturn(room);

        FeedbackRoomSummaryResult result = coachFeedbackRoomService.createRoom(member.getId());

        verify(memberFinderService).findById(member.getId());
        verify(feedbackRoomRepository).create(member, "새 운동 피드백");
        assertThat(result.roomId()).isEqualTo(room.getId());
        assertThat(result.title()).isEqualTo("새 운동 피드백");
        assertThat(result.pinned()).isFalse();
    }

    @Test
    @DisplayName("최근 피드백 방 목록을 조회한다")
    void findRecentRooms() {
        Member member = member();
        FeedbackRoom first = FeedbackRoomFixture.builder().member(member).title("러닝 피드백").build();
        FeedbackRoom second = FeedbackRoomFixture.builder().member(member).title("자전거 피드백").build();
        given(feedbackRoomRepository.findRecentByMemberId(member.getId()))
                .willReturn(List.of(first, second));

        List<FeedbackRoomSummaryResult> results = coachFeedbackRoomService.findRecentRooms(member.getId());

        verify(feedbackRoomRepository).findRecentByMemberId(member.getId());
        assertThat(results).extracting(FeedbackRoomSummaryResult::title)
                .containsExactly("러닝 피드백", "자전거 피드백");
    }

    @Test
    @DisplayName("고정된 피드백 방 목록을 조회한다")
    void findPinnedRooms() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).title("고정 피드백").build();
        room.pin();
        given(feedbackRoomRepository.findPinnedByMemberId(member.getId())).willReturn(List.of(room));

        List<FeedbackRoomSummaryResult> results = coachFeedbackRoomService.findPinnedRooms(member.getId());

        verify(feedbackRoomRepository).findPinnedByMemberId(member.getId());
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("고정 피드백");
        assertThat(results.get(0).pinned()).isTrue();
    }

    @Test
    @DisplayName("피드백 방 제목을 변경한다")
    void renameRoom() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).title("이전 제목").build();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(feedbackRoomRepository.save(room)).willReturn(room);

        FeedbackRoomSummaryResult result = coachFeedbackRoomService.renameRoom(member.getId(), room.getId(), "새 제목");

        verify(feedbackRoomRepository).save(room);
        assertThat(room.getTitle()).isEqualTo("새 제목");
        assertThat(result.title()).isEqualTo("새 제목");
    }

    @Test
    @DisplayName("피드백 방을 고정하고 고정 해제한다")
    void pinAndUnpinRoom() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(feedbackRoomRepository.save(room)).willReturn(room);

        FeedbackRoomSummaryResult pinned = coachFeedbackRoomService.pinRoom(member.getId(), room.getId());
        FeedbackRoomSummaryResult unpinned = coachFeedbackRoomService.unpinRoom(member.getId(), room.getId());

        assertThat(pinned.pinned()).isTrue();
        assertThat(unpinned.pinned()).isFalse();
        verify(feedbackRoomRepository, times(2)).save(room);
    }

    @Test
    @DisplayName("피드백 방을 삭제한다")
    void deleteRoom() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(feedbackRoomRepository.save(room)).willReturn(room);

        coachFeedbackRoomService.deleteRoom(member.getId(), room.getId());

        assertThat(room.getDeletedAt()).isNotNull();
        verify(feedbackRoomRepository).save(room);
    }

    @Test
    @DisplayName("피드백 방 상세와 메시지를 조회한다")
    void findRoom() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).title("러닝 피드백").build();
        FeedbackMessage userMessage = FeedbackMessageFixture.builder()
                .room(room)
                .content("운동 입력")
                .buildUserWorkoutMessage();
        FeedbackMessage assistantMessage = FeedbackMessageFixture.builder()
                .room(room)
                .content("AI 답변")
                .buildAssistantMessage();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(feedbackMessageRepository.findByRoomId(room.getId())).willReturn(List.of(userMessage, assistantMessage));

        FeedbackRoomDetailResult result = coachFeedbackRoomService.findRoom(member.getId(), room.getId());

        verify(feedbackMessageRepository).findByRoomId(room.getId());
        assertThat(result.roomId()).isEqualTo(room.getId());
        assertThat(result.messages()).hasSize(2);
        assertThat(result.messages()).extracting(message -> message.role())
                .containsExactly(FeedbackMessageRole.USER, FeedbackMessageRole.ASSISTANT);
    }

    @Test
    @DisplayName("피드백 방의 러닝 운동 데이터를 조회한다")
    void findRoomWorkoutsWithRunningWorkout() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        FeedbackRoomWorkout roomWorkout = FeedbackRoomWorkoutFixture.builder()
                .room(room)
                .workoutType(WorkOutType.RUNNING)
                .workoutId(10L)
                .build();
        RunningWorkout workout = RunningWorkoutFixture.builder().member(member).build();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(feedbackRoomWorkoutRepository.findByRoomId(room.getId())).willReturn(List.of(roomWorkout));
        given(runningWorkoutRepository.findByIdAndMemberId(10L, member.getId())).willReturn(Optional.of(workout));

        List<FeedbackRoomWorkoutResult> results = coachFeedbackRoomService.findRoomWorkouts(member.getId(), room.getId());

        verify(runningWorkoutRepository).findByIdAndMemberId(10L, member.getId());
        verifyNoInteractions(cyclingWorkoutRepository);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).workOutType()).isEqualTo(WorkOutType.RUNNING);
    }

    @Test
    @DisplayName("피드백 방의 자전거 운동 데이터를 조회한다")
    void findRoomWorkoutsWithCyclingWorkout() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        FeedbackRoomWorkout roomWorkout = FeedbackRoomWorkoutFixture.builder()
                .room(room)
                .workoutType(WorkOutType.CYCLING)
                .workoutId(20L)
                .build();
        CyclingWorkout workout = CyclingWorkoutFixture.builder().member(member).build();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(feedbackRoomWorkoutRepository.findByRoomId(room.getId())).willReturn(List.of(roomWorkout));
        given(cyclingWorkoutRepository.findByIdAndMemberId(20L, member.getId())).willReturn(Optional.of(workout));

        List<FeedbackRoomWorkoutResult> results = coachFeedbackRoomService.findRoomWorkouts(member.getId(), room.getId());

        verify(cyclingWorkoutRepository).findByIdAndMemberId(20L, member.getId());
        verifyNoInteractions(runningWorkoutRepository);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).workOutType()).isEqualTo(WorkOutType.CYCLING);
    }

    @Test
    @DisplayName("소유한 피드백 방이 없으면 조회에 실패한다")
    void findRoomWithoutOwnedRoomFails() {
        UUID roomId = UUID.randomUUID();
        given(feedbackRoomRepository.findByIdAndMemberId(roomId, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> coachFeedbackRoomService.findRoom(1L, roomId))
                .isInstanceOf(ProjectException.class);

        verifyNoInteractions(feedbackMessageRepository);
    }

    @Test
    @DisplayName("피드백 방 운동의 원본 운동 기록이 없으면 조회에 실패한다")
    void findRoomWorkoutsWithoutWorkoutFails() {
        Member member = member();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        FeedbackRoomWorkout roomWorkout = FeedbackRoomWorkoutFixture.builder()
                .room(room)
                .workoutType(WorkOutType.RUNNING)
                .workoutId(10L)
                .build();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(feedbackRoomWorkoutRepository.findByRoomId(room.getId())).willReturn(List.of(roomWorkout));
        given(runningWorkoutRepository.findByIdAndMemberId(10L, member.getId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> coachFeedbackRoomService.findRoomWorkouts(member.getId(), room.getId()))
                .isInstanceOf(ProjectException.class);
    }


    private Member member() {
        return MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
    }

}