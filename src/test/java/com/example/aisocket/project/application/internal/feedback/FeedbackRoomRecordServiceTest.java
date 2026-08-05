package com.example.aisocket.project.application.internal.feedback;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.out.FeedbackMessageRepository;
import com.example.aisocket.project.application.out.FeedbackRoomRepository;
import com.example.aisocket.project.application.out.FeedbackRoomWorkoutRepository;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.FeedbackMessage;
import com.example.aisocket.project.domain.FeedbackMessageRole;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.FeedbackRoomFixture;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class FeedbackRoomRecordServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private FeedbackRoomRecordService feedbackRoomRecordService;

    @MockitoBean
    private FeedbackRoomRepository feedbackRoomRepository;

    @MockitoBean
    private FeedbackMessageRepository feedbackMessageRepository;

    @MockitoBean
    private FeedbackRoomWorkoutRepository feedbackRoomWorkoutRepository;

    @Test
    @DisplayName("회원이 소유한 피드백 방을 조회한다")
    void findOwnedRoom() {
        Member member = MemberFixture.builder().id(1L).build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));

        FeedbackRoom foundRoom = feedbackRoomRecordService.findOwnedRoom(member.getId(), room.getId());

        assertThat(foundRoom).isSameAs(room);
    }

    @Test
    @DisplayName("회원이 소유한 피드백 방이 없으면 예외가 발생한다")
    void findOwnedRoomWithoutRoomFails() {
        UUID roomId = UUID.randomUUID();
        given(feedbackRoomRepository.findByIdAndMemberId(roomId, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackRoomRecordService.findOwnedRoom(1L, roomId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("사용자 운동 기록 메시지와 방 운동 연결을 저장한다")
    void saveUserWorkoutRecord() {
        Member member = MemberFixture.builder().id(1L).build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).title("새 운동 피드백").build();
        CoachFeedbackCommand command = runningCommand();
        given(feedbackRoomRepository.save(any(FeedbackRoom.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(feedbackMessageRepository.save(any(FeedbackMessage.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(feedbackRoomWorkoutRepository.save(any(FeedbackRoomWorkout.class))).willAnswer(invocation -> invocation.getArgument(0));

        feedbackRoomRecordService.saveUserWorkoutRecord(room, command, WorkOutType.RUNNING, 10L);

        ArgumentCaptor<FeedbackMessage> messageCaptor = ArgumentCaptor.forClass(FeedbackMessage.class);
        ArgumentCaptor<FeedbackRoomWorkout> roomWorkoutCaptor = ArgumentCaptor.forClass(FeedbackRoomWorkout.class);
        verify(feedbackRoomRepository).save(room);
        verify(feedbackMessageRepository).save(messageCaptor.capture());
        verify(feedbackRoomWorkoutRepository).save(roomWorkoutCaptor.capture());

        assertThat(room.getTitle()).isEqualTo("러닝 8.2km 피드백");
        assertThat(messageCaptor.getValue().getRole()).isEqualTo(FeedbackMessageRole.USER);
        assertThat(messageCaptor.getValue().getContent()).contains("운동 타입: 러닝", "거리: 8.2 km");
        assertThat(roomWorkoutCaptor.getValue().getRoom()).isSameAs(room);
        assertThat(roomWorkoutCaptor.getValue().getWorkoutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(roomWorkoutCaptor.getValue().getWorkoutId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("AI 피드백 메시지를 저장한다")
    void saveAssistantMessage() {
        FeedbackRoom room = FeedbackRoomFixture.builder().build();
        given(feedbackMessageRepository.save(any(FeedbackMessage.class))).willAnswer(invocation -> invocation.getArgument(0));

        feedbackRoomRecordService.saveAssistantMessage(room, WorkOutType.RUNNING, 10L, "좋은 운동입니다.");

        ArgumentCaptor<FeedbackMessage> messageCaptor = ArgumentCaptor.forClass(FeedbackMessage.class);
        verify(feedbackMessageRepository).save(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getRole()).isEqualTo(FeedbackMessageRole.ASSISTANT);
        assertThat(messageCaptor.getValue().getContent()).isEqualTo("좋은 운동입니다.");
    }

    private CoachFeedbackCommand runningCommand() {
        return new CoachFeedbackCommand(
                WorkOutType.RUNNING,
                AthleteTier.AMATEUR,
                RunningWorkoutFixture.builder().commonCommand(),
                RunningWorkoutFixture.builder().runningCommand(),
                null
        );
    }
}
