package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.in.WorkoutQueryService;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutQueryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class WorkoutQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @MockitoBean
    private WorkoutQueryService workoutQueryService;

    @Test
    @DisplayName("운동 기록 단건을 조회한다")
    void getWorkout() throws Exception {
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);
        given(workoutQueryService.getWorkout(member.getId(), WorkOutType.CYCLING, 10L))
                .willReturn(result());

        mockMvc.perform(get("/api/v1/workouts/records/CYCLING/10")
                        .cookie(new Cookie("accessToken", "access-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workoutId").value(10))
                .andExpect(jsonPath("$.workOutType").value("CYCLING"))
                .andExpect(jsonPath("$.tier").value("AMATEUR"))
                .andExpect(jsonPath("$.title").value("자전거 58.8km"))
                .andExpect(jsonPath("$.inputSource").value("FIT_FILE"))
                .andExpect(jsonPath("$.feedbackCount").value(2))
                .andExpect(jsonPath("$.distance").value(58.8))
                .andExpect(jsonPath("$.avgPower").value(180.0))
                .andExpect(jsonPath("$.samples[0].heartRate").value(150))
                .andExpect(jsonPath("$.samples[0].power").value(220));

        verify(workoutQueryService).getWorkout(member.getId(), WorkOutType.CYCLING, 10L);
    }

    private FeedbackRoomWorkoutResult result() {
        return new FeedbackRoomWorkoutResult(
                10L,
                WorkOutType.CYCLING,
                AthleteTier.AMATEUR,
                "자전거 58.8km",
                WorkoutInputSource.FIT_FILE,
                2L,
                LocalDateTime.parse("2026-06-17T09:57:00"),
                LocalDateTime.parse("2026-06-17T11:57:00"),
                58.8,
                750.0,
                220.0,
                7200,
                1300.0,
                82.0,
                105.0,
                178.0,
                141.0,
                29.4,
                54.0,
                180.0,
                620.0,
                250.0,
                null,
                null,
                null,
                List.of(new FeedbackRoomWorkoutResult.SensorSampleResult(
                        0,
                        0.0,
                        37.1,
                        127.1,
                        40.0,
                        150,
                        85,
                        8.2,
                        220
                ))
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
