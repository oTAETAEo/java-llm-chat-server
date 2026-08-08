package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
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

class WorkoutFeedbackCountServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private WorkoutFeedbackCountService workoutFeedbackCountService;

    @MockitoBean
    private RunningWorkoutRepository runningWorkoutRepository;

    @MockitoBean
    private CyclingWorkoutRepository cyclingWorkoutRepository;

    @Test
    @DisplayName("러닝 운동 피드백 횟수를 증가시킨다")
    void increaseRunningWorkoutFeedbackCount() {
        Member member = MemberFixture.builder().id(1L).build();
        RunningWorkout workout = RunningWorkoutFixture.builder().member(member).build();
        given(runningWorkoutRepository.findByIdAndMemberId(10L, member.getId()))
                .willReturn(Optional.of(workout));

        workoutFeedbackCountService.increase(member.getId(), WorkOutType.RUNNING, 10L);

        verify(runningWorkoutRepository).findByIdAndMemberId(10L, member.getId());
        verifyNoInteractions(cyclingWorkoutRepository);
        assertThat(workout.getFeedbackCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("자전거 운동 피드백 횟수를 증가시킨다")
    void increaseCyclingWorkoutFeedbackCount() {
        Member member = MemberFixture.builder().id(1L).build();
        CyclingWorkout workout = CyclingWorkoutFixture.builder().member(member).build();
        given(cyclingWorkoutRepository.findByIdAndMemberId(20L, member.getId()))
                .willReturn(Optional.of(workout));

        workoutFeedbackCountService.increase(member.getId(), WorkOutType.CYCLING, 20L);

        verify(cyclingWorkoutRepository).findByIdAndMemberId(20L, member.getId());
        verifyNoInteractions(runningWorkoutRepository);
        assertThat(workout.getFeedbackCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("운동 기록이 없으면 피드백 횟수 증가에 실패한다")
    void increaseWithoutWorkoutFails() {
        given(runningWorkoutRepository.findByIdAndMemberId(10L, 1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> workoutFeedbackCountService.increase(1L, WorkOutType.RUNNING, 10L))
                .isInstanceOf(ProjectException.class);
    }
}
