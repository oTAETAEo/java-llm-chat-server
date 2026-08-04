package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.out.AiSender;
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
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

class CoachFeedbackServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private CoachFeedbackService coachFeedbackService;

    @MockitoBean
    private WorkoutRecordRegisterService workoutRecordRegisterService;

    @MockitoBean
    private WorkoutVectorRegisterService workoutVectorRegisterService;

    @MockitoBean
    private AiSender aiSender;

    @MockitoBean
    private FeedbackRoomRepository feedbackRoomRepository;

    @MockitoBean
    private FeedbackMessageRepository feedbackMessageRepository;

    @MockitoBean
    private FeedbackRoomWorkoutRepository feedbackRoomWorkoutRepository;

    @Test
    @DisplayName("스트리밍 피드백 전에 운동 벡터 저장 유스케이스를 호출하고 AI 응답 조각을 전달한다")
    void getFeedbackStream() {

        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();


        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();

        CoachFeedbackCommand command = runningCommand();

        given(workoutRecordRegisterService.register(member.getId(), command))
                .willReturn(new WorkoutRecordRegistration(10L, member, workout));

        doAnswer(invocation -> {
            Consumer<String> consumer = invocation.getArgument(1);
            consumer.accept("첫 번째 응답");
            consumer.accept("두 번째 응답");
            return null;
        }).when(aiSender).sendStream(anyString(), any());

        List<String> chunks = new ArrayList<>();
        coachFeedbackService.getFeedbackStream(member.getId(), command, chunks::add);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);

        var inOrder = inOrder(workoutRecordRegisterService, workoutVectorRegisterService, aiSender);
        inOrder.verify(workoutRecordRegisterService).register(member.getId(), command);
        inOrder.verify(workoutVectorRegisterService).register(eq(member), any(WorkoutRecordRegistration.class), eq(AthleteTier.AMATEUR));
        inOrder.verify(aiSender).sendStream(promptCaptor.capture(), any());

        assertThat(workout).isInstanceOf(RunningWorkout.class);
        assertThat(promptCaptor.getValue()).contains("[기본 운동 통계 정보]", "[러닝 전용 분석 지표]");
        assertThat(chunks).containsExactly("첫 번째 응답", "두 번째 응답");
    }

    @Test
    @DisplayName("운동 벡터 저장에 실패하면 AI 스트리밍을 호출하지 않는다")
    void getFeedbackStreamWhenWorkoutVectorSaveFails() {

        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();

        CoachFeedbackCommand command = runningCommand();
        RuntimeException saveException = new RuntimeException("vector save failed");

        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();

        given(workoutRecordRegisterService.register(member.getId(), command))
                .willReturn(new WorkoutRecordRegistration(10L, member, workout));
        given(workoutVectorRegisterService.register(eq(member), any(WorkoutRecordRegistration.class), eq(AthleteTier.AMATEUR)))
                .willThrow(saveException);

        assertThatThrownBy(() ->
                coachFeedbackService.getFeedbackStream(member.getId(), command, chunk -> {})
        ).isSameAs(saveException);

        verify(workoutRecordRegisterService).register(member.getId(), command);
        verify(workoutVectorRegisterService).register(eq(member), any(WorkoutRecordRegistration.class), eq(AthleteTier.AMATEUR));
        verifyNoInteractions(aiSender);
    }

    @Test
    @DisplayName("AI 스트리밍 호출에 실패하면 예외를 상위로 전파한다")
    void getFeedbackStreamWhenAiStreamingFails() {

        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();

        CoachFeedbackCommand command = runningCommand();
        RuntimeException aiException = new RuntimeException("ai stream failed");

        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();

        given(workoutRecordRegisterService.register(member.getId(), command))
                .willReturn(new WorkoutRecordRegistration(10L, member, workout));

        doAnswer(invocation -> {
            throw aiException;
        }).when(aiSender).sendStream(anyString(), any());

        assertThatThrownBy(() ->
                coachFeedbackService.getFeedbackStream(member.getId(), command, chunk -> {})
        ).isSameAs(aiException);

        verify(workoutRecordRegisterService).register(member.getId(), command);
        verify(workoutVectorRegisterService)
                .register(eq(member), any(WorkoutRecordRegistration.class), eq(AthleteTier.AMATEUR));
        verify(aiSender).sendStream(anyString(), any());
    }

    @Test
    @DisplayName("운동 기록 저장에 실패하면 벡터 저장과 AI 스트리밍을 호출하지 않는다")
    void getFeedbackStreamWhenWorkoutRecordFails() {

        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();

        CoachFeedbackCommand command = runningCommand();
        RuntimeException recordException = new RuntimeException("record failed");
        given(workoutRecordRegisterService.register(member.getId(), command))
                .willThrow(recordException);
        assertThatThrownBy(() ->
                coachFeedbackService.getFeedbackStream(member.getId(), command, chunk -> {})
        ).isSameAs(recordException);

        verify(workoutRecordRegisterService).register(member.getId(), command);
        verifyNoInteractions(workoutVectorRegisterService, aiSender);
    }

    @Test
    @DisplayName("방 단건 운동 피드백 스트림을 생성하고 사용자 메시지, 방 운동 연결, AI 메시지를 저장한다")
    void generateSingleWorkoutFeedbackStream() {
        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).title("새 운동 피드백").build();
        CoachFeedbackCommand command = runningCommand();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();
        WorkoutRecordRegistration registration = new WorkoutRecordRegistration(10L, member, workout);
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(workoutRecordRegisterService.register(member.getId(), command)).willReturn(registration);
        given(feedbackRoomRepository.save(any(FeedbackRoom.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(feedbackMessageRepository.save(any(FeedbackMessage.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(feedbackRoomWorkoutRepository.save(any(FeedbackRoomWorkout.class))).willAnswer(invocation -> invocation.getArgument(0));
        doAnswer(invocation -> {
            Consumer<String> consumer = invocation.getArgument(1);
            consumer.accept("첫 ");
            consumer.accept("응답");
            return null;
        }).when(aiSender).sendStream(anyString(), any());

        List<String> chunks = new ArrayList<>();
        coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, chunks::add);

        ArgumentCaptor<FeedbackMessage> messageCaptor = ArgumentCaptor.forClass(FeedbackMessage.class);
        ArgumentCaptor<FeedbackRoomWorkout> roomWorkoutCaptor = ArgumentCaptor.forClass(FeedbackRoomWorkout.class);
        verify(workoutRecordRegisterService).register(member.getId(), command);
        verify(workoutVectorRegisterService).register(member, registration, AthleteTier.AMATEUR);
        verify(feedbackRoomRepository).save(room);
        verify(feedbackMessageRepository, times(2)).save(messageCaptor.capture());
        verify(feedbackRoomWorkoutRepository).save(roomWorkoutCaptor.capture());
        verify(aiSender).sendStream(anyString(), any());

        List<FeedbackMessage> savedMessages = messageCaptor.getAllValues();
        assertThat(room.getTitle()).isEqualTo("러닝 8.2km 피드백");
        assertThat(savedMessages.get(0).getRole()).isEqualTo(FeedbackMessageRole.USER);
        assertThat(savedMessages.get(0).getContent()).contains("운동 타입: 러닝", "거리: 8.2 km");
        assertThat(savedMessages.get(1).getRole()).isEqualTo(FeedbackMessageRole.ASSISTANT);
        assertThat(savedMessages.get(1).getContent()).isEqualTo("첫 응답");
        assertThat(roomWorkoutCaptor.getValue().getRoom()).isSameAs(room);
        assertThat(roomWorkoutCaptor.getValue().getWorkoutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(roomWorkoutCaptor.getValue().getWorkoutId()).isEqualTo(10L);
        assertThat(chunks).containsExactly("첫 ", "응답");
    }

    @Test
    @DisplayName("방 단건 운동 피드백 스트림의 AI 응답이 비어 있으면 AI 메시지를 저장하지 않는다")
    void generateSingleWorkoutFeedbackStreamWithEmptyAiResponse() {
        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        CoachFeedbackCommand command = runningCommand();
        RunningWorkout workout = RunningWorkoutFixture.builder().member(member).build();
        WorkoutRecordRegistration registration = new WorkoutRecordRegistration(10L, member, workout);
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(workoutRecordRegisterService.register(member.getId(), command)).willReturn(registration);
        given(feedbackRoomRepository.save(any(FeedbackRoom.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(feedbackMessageRepository.save(any(FeedbackMessage.class))).willAnswer(invocation -> invocation.getArgument(0));
        doAnswer(invocation -> null).when(aiSender).sendStream(anyString(), any());

        coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, chunk -> {});

        ArgumentCaptor<FeedbackMessage> messageCaptor = ArgumentCaptor.forClass(FeedbackMessage.class);
        verify(feedbackMessageRepository).save(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getRole()).isEqualTo(FeedbackMessageRole.USER);
    }

    @Test
    @DisplayName("방 단건 운동 피드백 스트림 생성 시 운동 기록 저장에 실패하면 벡터 저장과 AI 스트리밍을 호출하지 않는다")
    void generateSingleWorkoutFeedbackStreamWhenWorkoutRecordFails() {
        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        CoachFeedbackCommand command = runningCommand();
        RuntimeException exception = new RuntimeException("record failed");
        given(feedbackRoomRepository.findByIdAndMemberId(room.getId(), member.getId())).willReturn(Optional.of(room));
        given(workoutRecordRegisterService.register(member.getId(), command)).willThrow(exception);

        assertThatThrownBy(() -> coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, chunk -> {}))
                .isSameAs(exception);

        verify(workoutRecordRegisterService).register(member.getId(), command);
        verifyNoInteractions(workoutVectorRegisterService, aiSender);
        verify(feedbackMessageRepository, never()).save(any(FeedbackMessage.class));
        verify(feedbackRoomWorkoutRepository, never()).save(any(FeedbackRoomWorkout.class));
    }

    @Test
    @DisplayName("소유한 피드백 방이 없으면 방 단건 운동 피드백 스트림 생성에 실패한다")
    void generateSingleWorkoutFeedbackStreamWithoutOwnedRoomFails() {
        UUID roomId = UUID.randomUUID();
        CoachFeedbackCommand command = runningCommand();
        given(feedbackRoomRepository.findByIdAndMemberId(roomId, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> coachFeedbackService.generateSingleWorkoutFeedbackStream(1L, roomId, command, chunk -> {}))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(workoutRecordRegisterService, workoutVectorRegisterService, aiSender);
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
