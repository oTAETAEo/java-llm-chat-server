package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.out.AiSender;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class CoachFeedbackTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private CoachFeedback coachFeedback;

    @MockitoBean
    private WorkoutRecordSaver workoutRecordSaver;

    @MockitoBean
    private WorkoutVectorSaver workoutVectorSaver;

    @MockitoBean
    private AiSender aiSender;

    @Test
    @DisplayName("스트리밍 피드백 전에 운동 벡터 저장 유스케이스를 호출하고 AI 응답 조각을 전달한다")
    void getFeedbackStream() {
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        RunningWorkout workout = runningWorkout();
        given(workoutRecordSaver.save(member, workout, AthleteTier.AMATEUR))
                .willReturn(10L);
        doAnswer(invocation -> {
            Consumer<String> consumer = invocation.getArgument(1);
            consumer.accept("첫 번째 응답");
            consumer.accept("두 번째 응답");
            return null;
        }).when(aiSender).sendStream(anyString(), any());
        List<String> chunks = new ArrayList<>();

        coachFeedback.getFeedbackStream(member, workout, AthleteTier.AMATEUR, chunks::add);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        var inOrder = inOrder(workoutRecordSaver, workoutVectorSaver, aiSender);
        inOrder.verify(workoutRecordSaver).save(member, workout, AthleteTier.AMATEUR);
        inOrder.verify(workoutVectorSaver).save(member, 10L, workout, AthleteTier.AMATEUR);
        inOrder.verify(aiSender).sendStream(promptCaptor.capture(), any());
        assertThat(promptCaptor.getValue()).contains("[기본 운동 통계 정보]", "[러닝 전용 분석 지표]");
        assertThat(chunks).containsExactly("첫 번째 응답", "두 번째 응답");
    }

    @Test
    @DisplayName("운동 벡터 저장에 실패하면 AI 스트리밍을 호출하지 않는다")
    void getFeedbackStreamWhenWorkoutVectorSaveFails() {
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        RunningWorkout workout = runningWorkout();
        RuntimeException saveException = new RuntimeException("vector save failed");
        given(workoutRecordSaver.save(member, workout, AthleteTier.AMATEUR))
                .willReturn(10L);
        given(workoutVectorSaver.save(member, 10L, workout, AthleteTier.AMATEUR))
                .willThrow(saveException);

        assertThatThrownBy(() ->
                coachFeedback.getFeedbackStream(member, workout, AthleteTier.AMATEUR, chunk -> {
                })
        ).isSameAs(saveException);

        verify(workoutRecordSaver).save(member, workout, AthleteTier.AMATEUR);
        verify(workoutVectorSaver).save(member, 10L, workout, AthleteTier.AMATEUR);
        verifyNoInteractions(aiSender);
    }

    @Test
    @DisplayName("AI 스트리밍 호출에 실패하면 예외를 상위로 전파한다")
    void getFeedbackStreamWhenAiStreamingFails() {
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        RunningWorkout workout = runningWorkout();
        RuntimeException aiException = new RuntimeException("ai stream failed");
        given(workoutRecordSaver.save(member, workout, AthleteTier.AMATEUR))
                .willReturn(10L);
        doAnswer(invocation -> {
            throw aiException;
        }).when(aiSender).sendStream(anyString(), any());

        assertThatThrownBy(() ->
                coachFeedback.getFeedbackStream(member, workout, AthleteTier.AMATEUR, chunk -> {
                })
        ).isSameAs(aiException);

        verify(workoutRecordSaver).save(member, workout, AthleteTier.AMATEUR);
        verify(workoutVectorSaver).save(member, 10L, workout, AthleteTier.AMATEUR);
        verify(aiSender).sendStream(anyString(), any());
    }

    @Test
    @DisplayName("운동 기록 저장에 실패하면 벡터 저장과 AI 스트리밍을 호출하지 않는다")
    void getFeedbackStreamWhenWorkoutRecordFails() {
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        RunningWorkout workout = runningWorkout();
        RuntimeException recordException = new RuntimeException("record failed");
        given(workoutRecordSaver.save(member, workout, AthleteTier.AMATEUR))
                .willThrow(recordException);

        assertThatThrownBy(() ->
                coachFeedback.getFeedbackStream(member, workout, AthleteTier.AMATEUR, chunk -> {
                })
        ).isSameAs(recordException);

        verify(workoutRecordSaver).save(member, workout, AthleteTier.AMATEUR);
        verifyNoInteractions(workoutVectorSaver, aiSender);
    }

    private RunningWorkout runningWorkout() {
        return RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                new CreateCommonWorkoutCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        8.2,
                        120.0,
                        85.0,
                        45,
                        530.0,
                        172.0,
                        188.0,
                        176.0,
                        148.0
                ),
                new CreateRunningWorkoutCommand(
                        5.48,
                        4.92,
                        7600
                )
        );
    }



}
