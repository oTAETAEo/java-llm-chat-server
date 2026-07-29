package com.example.aisocket.project.application.service;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.application.in.CoachFeedbackService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
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

    @Test
    @DisplayName("스트리밍 피드백 전에 운동 벡터 저장 유스케이스를 호출하고 AI 응답 조각을 전달한다")
    void getFeedbackStream() {

        Member member = MemberFixture.builder()
                .id(1L)
                .nickname("runner")
                .build();

        CoachFeedbackCommand command = runningCommand();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();

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
        assertThat(workout).isInstanceOf(RunningWorkout.class);
        inOrder.verify(aiSender).sendStream(promptCaptor.capture(), any());
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
        verify(workoutVectorRegisterService)
                .register(eq(member), any(WorkoutRecordRegistration.class), eq(AthleteTier.AMATEUR));
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
