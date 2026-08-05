package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.CyclingWorkoutSensorDataRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutSensorDataRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class FeedbackRoomWorkoutQueryServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private FeedbackRoomWorkoutQueryService feedbackRoomWorkoutQueryService;

    @MockitoBean
    private RunningWorkoutRepository runningWorkoutRepository;

    @MockitoBean
    private CyclingWorkoutRepository cyclingWorkoutRepository;

    @MockitoBean
    private RunningWorkoutSensorDataRepository runningWorkoutSensorDataRepository;

    @MockitoBean
    private CyclingWorkoutSensorDataRepository cyclingWorkoutSensorDataRepository;

    @Test
    @DisplayName("러닝 방 운동이면 러닝 운동과 센서 데이터를 조회한다")
    void findRunningWorkout() {
        Member member = MemberFixture.builder().id(1L).build();
        FeedbackRoomWorkout roomWorkout = FeedbackRoomWorkoutFixture.builder()
                .workoutType(WorkOutType.RUNNING)
                .workoutId(10L)
                .build();
        RunningWorkout workout = RunningWorkoutFixture.builder().member(member).build();
        given(runningWorkoutRepository.findByIdAndMemberId(10L, member.getId())).willReturn(Optional.of(workout));
        given(runningWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workout.getId()))
                .willReturn(Optional.of("""
                        [{"elapsedSeconds":0,"heartRate":150,"latitude":37.1}]
                        """));

        FeedbackRoomWorkoutResult result = feedbackRoomWorkoutQueryService.findWorkout(member.getId(), roomWorkout);

        verify(runningWorkoutRepository).findByIdAndMemberId(10L, member.getId());
        verify(runningWorkoutSensorDataRepository).findSamplesJsonByWorkoutId(workout.getId());
        verifyNoInteractions(cyclingWorkoutRepository);
        assertThat(result.workOutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(result.samples()).hasSize(1);
        assertThat(result.samples().get(0).heartRate()).isEqualTo(150);
        assertThat(result.samples().get(0).latitude()).isEqualTo(37.1);
    }

    @Test
    @DisplayName("자전거 방 운동이면 자전거 운동 저장소에서 조회한다")
    void findCyclingWorkout() {
        Member member = MemberFixture.builder().id(1L).build();
        FeedbackRoomWorkout roomWorkout = FeedbackRoomWorkoutFixture.builder()
                .workoutType(WorkOutType.CYCLING)
                .workoutId(20L)
                .build();
        CyclingWorkout workout = CyclingWorkoutFixture.builder().member(member).build();
        given(cyclingWorkoutRepository.findByIdAndMemberId(20L, member.getId())).willReturn(Optional.of(workout));
        given(cyclingWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workout.getId()))
                .willReturn(Optional.empty());

        FeedbackRoomWorkoutResult result = feedbackRoomWorkoutQueryService.findWorkout(member.getId(), roomWorkout);

        verify(cyclingWorkoutRepository).findByIdAndMemberId(20L, member.getId());
        verify(cyclingWorkoutSensorDataRepository).findSamplesJsonByWorkoutId(workout.getId());
        verifyNoInteractions(runningWorkoutRepository);
        assertThat(result.workOutType()).isEqualTo(WorkOutType.CYCLING);
        assertThat(result.samples()).isEmpty();
    }

    @Test
    @DisplayName("방 운동의 원본 운동 기록이 없으면 조회에 실패한다")
    void findWorkoutWithoutWorkoutFails() {
        Member member = MemberFixture.builder().id(1L).build();
        FeedbackRoomWorkout roomWorkout = FeedbackRoomWorkoutFixture.builder()
                .workoutType(WorkOutType.RUNNING)
                .workoutId(10L)
                .build();
        given(runningWorkoutRepository.findByIdAndMemberId(10L, member.getId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackRoomWorkoutQueryService.findWorkout(member.getId(), roomWorkout))
                .isInstanceOf(ProjectException.class);
    }
}
