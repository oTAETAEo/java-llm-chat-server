package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.command.WorkoutDashboardFilterCommand;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WorkoutDashboardServiceImplTest {

    @Mock
    private RunningWorkoutRepository runningWorkoutRepository;

    @Mock
    private CyclingWorkoutRepository cyclingWorkoutRepository;

    @InjectMocks
    private WorkoutDashboardServiceImpl workoutDashboardService;

    @Test
    @DisplayName("운동 기록 인사이트를 조회한다")
    void getInsights() {
        Member member = MemberFixture.builder().id(1L).build();
        RunningWorkout fridayRun = runningWorkout(member, LocalDateTime.parse("2026-08-14T07:00:00"), 10.0, 5.0, 1);
        RunningWorkout saturdayRun = runningWorkout(member, LocalDateTime.parse("2026-08-15T07:00:00"), 5.0, 6.0, 0);
        CyclingWorkout saturdayRide = cyclingWorkout(member, LocalDateTime.parse("2026-08-15T09:00:00"), 35.0, 180.0, 2);
        WorkoutDashboardFilterCommand filter = new WorkoutDashboardFilterCommand("ALL", "ALL", null, null);

        given(runningWorkoutRepository.findDashboardWorkouts(member.getId(), null, null))
                .willReturn(List.of(fridayRun, saturdayRun));
        given(cyclingWorkoutRepository.findDashboardWorkouts(member.getId(), null, null))
                .willReturn(List.of(saturdayRide));

        WorkoutDashboardInsightResult result = workoutDashboardService.getInsights(member.getId(), filter);

        assertThat(result.typeDistribution().totalWorkoutCount()).isEqualTo(3);
        assertThat(result.typeDistribution().totalDistance()).isEqualTo(50.0);
        assertThat(result.typeDistribution().running().workOutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(result.typeDistribution().running().count()).isEqualTo(2);
        assertThat(result.typeDistribution().running().distance()).isEqualTo(15.0);
        assertThat(result.typeDistribution().running().workoutRatio()).isCloseTo(200.0 / 3.0, within(0.000001));
        assertThat(result.typeDistribution().running().distanceRatio()).isEqualTo(30.0);
        assertThat(result.typeDistribution().cycling().workOutType()).isEqualTo(WorkOutType.CYCLING);
        assertThat(result.typeDistribution().cycling().count()).isEqualTo(1);
        assertThat(result.typeDistribution().cycling().distance()).isEqualTo(35.0);
        assertThat(result.typeDistribution().cycling().workoutRatio()).isCloseTo(100.0 / 3.0, within(0.000001));
        assertThat(result.typeDistribution().cycling().distanceRatio()).isEqualTo(70.0);
        assertThat(result.typeDistribution().avgRunningPace()).isEqualTo(5.5);
        assertThat(result.typeDistribution().avgCyclingPower()).isEqualTo(180.0);

        assertThat(result.workoutFrequency().maxCount()).isEqualTo(2);
        assertThat(result.workoutFrequency().days()).hasSize(7);
        assertThat(result.workoutFrequency().days())
                .filteredOn(day -> day.dayOfWeek() == DayOfWeek.FRIDAY)
                .singleElement()
                .extracting(WorkoutDashboardInsightResult.WorkoutDayFrequencyResult::count)
                .isEqualTo(1);
        assertThat(result.workoutFrequency().days())
                .filteredOn(day -> day.dayOfWeek() == DayOfWeek.SATURDAY)
                .singleElement()
                .extracting(WorkoutDashboardInsightResult.WorkoutDayFrequencyResult::count)
                .isEqualTo(2);

        assertThat(result.feedbackUsage().totalWorkoutCount()).isEqualTo(3);
        assertThat(result.feedbackUsage().feedbackUsedWorkoutCount()).isEqualTo(2);
        assertThat(result.feedbackUsage().totalFeedbackCount()).isEqualTo(3);
        assertThat(result.feedbackUsage().usageRate()).isCloseTo(200.0 / 3.0, within(0.000001));
    }

    private RunningWorkout runningWorkout(Member member, LocalDateTime startedAt, double distance, double avgPace, int feedbackCount) {
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .startedAt(startedAt)
                .endedAt(startedAt.plusHours(1))
                .distance(distance)
                .avgPace(avgPace)
                .build();
        increaseFeedbackCount(workout, feedbackCount);
        return workout;
    }

    private CyclingWorkout cyclingWorkout(Member member, LocalDateTime startedAt, double distance, double avgPower, int feedbackCount) {
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .member(member)
                .startedAt(startedAt)
                .endedAt(startedAt.plusHours(1))
                .distance(distance)
                .avgPower(avgPower)
                .build();
        increaseFeedbackCount(workout, feedbackCount);
        return workout;
    }

    private void increaseFeedbackCount(RunningWorkout workout, int count) {
        for (int index = 0; index < count; index += 1) {
            workout.increaseFeedbackCount();
        }
    }

    private void increaseFeedbackCount(CyclingWorkout workout, int count) {
        for (int index = 0; index < count; index += 1) {
            workout.increaseFeedbackCount();
        }
    }
}
