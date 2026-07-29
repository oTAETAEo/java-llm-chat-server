package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.dto.result.FeedbackRoomSummaryResult;
import com.example.aisocket.project.application.in.CoachFeedbackRoomService;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoachFeedbackRoomController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class CoachFeedbackRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @MockitoBean
    private CoachFeedbackRoomService coachFeedbackRoomService;

    @Test
    @DisplayName("피드백 방을 생성한다")
    void createRoom() throws Exception {
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);
        UUID roomId = UUID.fromString("6a69e3a4-9d98-83ee-932f-d69cd46770a0");
        given(coachFeedbackRoomService.createRoom(member.getId()))
                .willReturn(new FeedbackRoomSummaryResult(roomId, "새 운동 피드백", false, LocalDateTime.now(), LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/coach/feedback/rooms")
                        .cookie(new Cookie("accessToken", "access-token")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").value(roomId.toString()))
                .andExpect(jsonPath("$.title").value("새 운동 피드백"));
    }

    @Test
    @DisplayName("최근 피드백 방 목록을 조회한다")
    void findRecentRooms() throws Exception {
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);
        UUID roomId = UUID.fromString("6a69e3a4-9d98-83ee-932f-d69cd46770a0");
        given(coachFeedbackRoomService.findRecentRooms(member.getId()))
                .willReturn(java.util.List.of(new FeedbackRoomSummaryResult(roomId, "러닝 22.0km 피드백", false, LocalDateTime.now(), LocalDateTime.now())));

        mockMvc.perform(get("/api/v1/coach/feedback/rooms/recent")
                        .cookie(new Cookie("accessToken", "access-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomId").value(roomId.toString()))
                .andExpect(jsonPath("$[0].title").value("러닝 22.0km 피드백"));
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