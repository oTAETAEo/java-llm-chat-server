package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.dto.command.WorkoutDashboardFilterCommand;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.FeedbackUsageResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutDayFrequencyResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutFrequencyResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutTypeDistributionResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutTypeShareResult;
import com.example.aisocket.project.application.in.WorkoutDashboardService;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.WorkOutType;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.closeTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutDashboardController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class WorkoutDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @MockitoBean
    private WorkoutDashboardService workoutDashboardService;

    @Test
    @DisplayName("운동 기록 인사이트를 조회한다")
    void getInsights() throws Exception {
        Member member = authenticatedMember();
        WorkoutDashboardFilterCommand filter = new WorkoutDashboardFilterCommand("30d", "ALL", null, null);
        givenAuthenticatedMember(member);
        given(workoutDashboardService.getInsights(member.getId(), filter))
                .willReturn(result());

        mockMvc.perform(get("/api/v1/workouts/dashboard/insights")
                        .param("period", "30d")
                        .param("workOutType", "ALL")
                        .cookie(new Cookie("accessToken", "access-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeDistribution.totalWorkoutCount").value(3))
                .andExpect(jsonPath("$.typeDistribution.totalDistance").value(50.0))
                .andExpect(jsonPath("$.typeDistribution.running.workOutType").value("RUNNING"))
                .andExpect(jsonPath("$.typeDistribution.running.count").value(2))
                .andExpect(jsonPath("$.typeDistribution.running.distanceRatio").value(30.0))
                .andExpect(jsonPath("$.typeDistribution.cycling.workOutType").value("CYCLING"))
                .andExpect(jsonPath("$.typeDistribution.cycling.count").value(1))
                .andExpect(jsonPath("$.typeDistribution.avgRunningPace").value(5.5))
                .andExpect(jsonPath("$.typeDistribution.avgCyclingPower").value(180.0))
                .andExpect(jsonPath("$.workoutFrequency.maxCount").value(2))
                .andExpect(jsonPath("$.workoutFrequency.days[4].dayOfWeek").value("FRIDAY"))
                .andExpect(jsonPath("$.workoutFrequency.days[4].count").value(1))
                .andExpect(jsonPath("$.workoutFrequency.days[5].dayOfWeek").value("SATURDAY"))
                .andExpect(jsonPath("$.workoutFrequency.days[5].count").value(2))
                .andExpect(jsonPath("$.feedbackUsage.totalWorkoutCount").value(3))
                .andExpect(jsonPath("$.feedbackUsage.feedbackUsedWorkoutCount").value(2))
                .andExpect(jsonPath("$.feedbackUsage.totalFeedbackCount").value(3))
                .andExpect(jsonPath("$.feedbackUsage.usageRate").value(closeTo(200.0 / 3.0, 0.000001)));

        verify(workoutDashboardService).getInsights(eq(member.getId()), eq(filter));
    }

    private WorkoutDashboardInsightResult result() {
        return new WorkoutDashboardInsightResult(
                new WorkoutTypeDistributionResult(
                        3,
                        50.0,
                        new WorkoutTypeShareResult(WorkOutType.RUNNING, 2, 15.0, 200.0 / 3.0, 30.0),
                        new WorkoutTypeShareResult(WorkOutType.CYCLING, 1, 35.0, 100.0 / 3.0, 70.0),
                        5.5,
                        180.0
                ),
                new WorkoutFrequencyResult(
                        2,
                        Arrays.stream(DayOfWeek.values())
                                .map(dayOfWeek -> new WorkoutDayFrequencyResult(
                                        dayOfWeek,
                                        switch (dayOfWeek) {
                                            case FRIDAY -> 1;
                                            case SATURDAY -> 2;
                                            default -> 0;
                                        }
                                ))
                                .toList()
                ),
                new FeedbackUsageResult(3, 2, 3, 200.0 / 3.0)
        );
    }

    private Member authenticatedMember() {
        return MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .nickname("runner")
                .build();
    }

    private void givenAuthenticatedMember(Member member) {
        given(jwtTokenValidator.validateAccessToken("access-token"))
                .willReturn(new JwtTokenClaims(member.getId(), member.getEmail(), member.getNickname(), "access", Instant.parse("2026-08-24T00:30:00Z")));
        given(accessTokenBlacklistService.isBlacklisted("access-token")).willReturn(false);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
    }
}
