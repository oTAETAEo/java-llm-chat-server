package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.internal.feedback.FeedbackRoomRecordService;
import com.example.aisocket.project.application.internal.workout.WorkoutFeedbackCountService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.internal.workout.WorkoutSensorDataRegisterService;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.FeedbackRoom;
import com.example.aisocket.project.domain.FeedbackRoomFixture;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    private FeedbackRoomRecordService feedbackRoomRecordService;

    @MockitoBean
    private WorkoutSensorDataRegisterService workoutSensorDataRegisterService;

    @MockitoBean
    private WorkoutFeedbackCountService workoutFeedbackCountService;

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
        given(feedbackRoomRecordService.findOwnedRoom(member.getId(), room.getId())).willReturn(room);
        given(workoutRecordRegisterService.register(member.getId(), command)).willReturn(registration);
        doAnswer(invocation -> {
            Consumer<String> consumer = invocation.getArgument(1);
            consumer.accept("첫 ");
            consumer.accept("응답");
            return null;
        }).when(aiSender).sendStream(anyString(), any());

        List<String> chunks = new ArrayList<>();
        coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, chunks::add);

        verify(workoutRecordRegisterService).register(member.getId(), command);
        verify(workoutVectorRegisterService).register(member, registration, AthleteTier.AMATEUR);
        verify(feedbackRoomRecordService).saveUserWorkoutRecord(room, command, WorkOutType.RUNNING, 10L);
        verify(workoutFeedbackCountService).increase(member.getId(), WorkOutType.RUNNING, 10L);
        verify(feedbackRoomRecordService).saveAssistantMessage(room, WorkOutType.RUNNING, 10L, "첫 응답");
        verify(aiSender).sendStream(anyString(), any());

        assertThat(chunks).containsExactly("첫 ", "응답");
    }

    @Test
    @DisplayName("v2 방 단건 운동 피드백 스트림 생성 시 센서 데이터 저장 유스케이스를 호출한다")
    void generateSingleWorkoutFeedbackStreamWithSensorData() {
        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        CoachFeedbackCommand command = runningCommand();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();
        WorkoutRecordRegistration registration = new WorkoutRecordRegistration(10L, member, workout);
        CreateWorkoutSensorDataCommand sensorCommand = new CreateWorkoutSensorDataCommand(
                "[{\"elapsedSeconds\":0,\"heartRate\":150,\"latitude\":37.1}]"
        );

        given(feedbackRoomRecordService.findOwnedRoom(member.getId(), room.getId())).willReturn(room);
        given(workoutRecordRegisterService.register(member.getId(), command)).willReturn(registration);
        doAnswer(invocation -> null).when(aiSender).sendStream(anyString(), any());

        coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, sensorCommand, chunk -> {});

        verify(workoutSensorDataRegisterService).register(registration, sensorCommand);
    }

    @Test
    @DisplayName("중복 운동이면 기존 운동 ID로 피드백을 연결하고 벡터와 센서 데이터는 새로 저장하지 않는다")
    void generateSingleWorkoutFeedbackStreamWithDuplicateWorkout() {
        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        CoachFeedbackCommand command = runningCommand();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();
        WorkoutRecordRegistration registration = new WorkoutRecordRegistration(10L, member, workout, false);
        CreateWorkoutSensorDataCommand sensorCommand = new CreateWorkoutSensorDataCommand(
                "[{\"elapsedSeconds\":0,\"heartRate\":150}]"
        );

        given(feedbackRoomRecordService.findOwnedRoom(member.getId(), room.getId())).willReturn(room);
        given(workoutRecordRegisterService.register(member.getId(), command)).willReturn(registration);
        doAnswer(invocation -> {
            Consumer<String> consumer = invocation.getArgument(1);
            consumer.accept("기존 운동 피드백");
            return null;
        }).when(aiSender).sendStream(anyString(), any());

        List<String> chunks = new ArrayList<>();
        coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, sensorCommand, chunks::add);

        verify(workoutRecordRegisterService).register(member.getId(), command);
        verify(workoutVectorRegisterService, never()).register(any(), any(), any());
        verify(workoutSensorDataRegisterService).register(registration, sensorCommand);
        verify(feedbackRoomRecordService).saveUserWorkoutRecord(room, command, WorkOutType.RUNNING, 10L);
        verify(workoutFeedbackCountService).increase(member.getId(), WorkOutType.RUNNING, 10L);
        verify(feedbackRoomRecordService).saveAssistantMessage(room, WorkOutType.RUNNING, 10L, "기존 운동 피드백");
        assertThat(chunks).containsExactly("기존 운동 피드백");
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
        given(feedbackRoomRecordService.findOwnedRoom(member.getId(), room.getId())).willReturn(room);
        given(workoutRecordRegisterService.register(member.getId(), command)).willReturn(registration);
        doAnswer(invocation -> null).when(aiSender).sendStream(anyString(), any());

        coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, chunk -> {});

        verify(feedbackRoomRecordService).saveUserWorkoutRecord(room, command, WorkOutType.RUNNING, 10L);
        verify(feedbackRoomRecordService, never()).saveAssistantMessage(any(), any(), any(), anyString());
    }

    @Test
    @DisplayName("클라이언트 스트림 전송이 실패해도 AI 응답은 끝까지 저장한다")
    void generateSingleWorkoutFeedbackStreamSavesAiResponseWhenChunkConsumerFails() {
        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();
        FeedbackRoom room = FeedbackRoomFixture.builder().member(member).build();
        CoachFeedbackCommand command = runningCommand();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();
        WorkoutRecordRegistration registration = new WorkoutRecordRegistration(10L, member, workout);
        given(feedbackRoomRecordService.findOwnedRoom(member.getId(), room.getId())).willReturn(room);
        given(workoutRecordRegisterService.register(member.getId(), command)).willReturn(registration);
        doAnswer(invocation -> {
            Consumer<String> consumer = invocation.getArgument(1);
            consumer.accept("끊겨도 ");
            consumer.accept("저장");
            return null;
        }).when(aiSender).sendStream(anyString(), any());

        coachFeedbackService.generateSingleWorkoutFeedbackStream(
                member.getId(),
                room.getId(),
                command,
                chunk -> {
                    throw new IllegalStateException("broken pipe");
                }
        );

        verify(feedbackRoomRecordService).saveAssistantMessage(room, WorkOutType.RUNNING, 10L, "끊겨도 저장");
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
        given(feedbackRoomRecordService.findOwnedRoom(member.getId(), room.getId())).willReturn(room);
        given(workoutRecordRegisterService.register(member.getId(), command)).willThrow(exception);

        assertThatThrownBy(() -> coachFeedbackService.generateSingleWorkoutFeedbackStream(member.getId(), room.getId(), command, chunk -> {}))
                .isSameAs(exception);

        verify(workoutRecordRegisterService).register(member.getId(), command);
        verifyNoInteractions(workoutVectorRegisterService, workoutFeedbackCountService, aiSender);
        verify(feedbackRoomRecordService, never()).saveUserWorkoutRecord(any(), any(), any(), any());
    }

    @Test
    @DisplayName("소유한 피드백 방이 없으면 방 단건 운동 피드백 스트림 생성에 실패한다")
    void generateSingleWorkoutFeedbackStreamWithoutOwnedRoomFails() {
        UUID roomId = UUID.randomUUID();
        CoachFeedbackCommand command = runningCommand();
        given(feedbackRoomRecordService.findOwnedRoom(1L, roomId)).willThrow(new RuntimeException("room not found"));

        assertThatThrownBy(() -> coachFeedbackService.generateSingleWorkoutFeedbackStream(1L, roomId, command, chunk -> {}))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(workoutRecordRegisterService, workoutVectorRegisterService, workoutFeedbackCountService, aiSender);
    }

    private CoachFeedbackCommand runningCommand() {
        return new CoachFeedbackCommand(
                WorkOutType.RUNNING,
                AthleteTier.AMATEUR,
                RunningWorkoutFixture.builder().commonCommand(),
                RunningWorkoutFixture.builder().runningCommand(),
                null,
                null,
                WorkoutInputSource.DIRECT_INPUT
        );
    }

}
