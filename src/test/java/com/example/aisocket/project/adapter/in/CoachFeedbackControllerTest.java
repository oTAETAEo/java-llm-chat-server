package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.in.CoachFeedbackService;
import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoachFeedbackController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class CoachFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @MockitoBean
    private CoachFeedbackService coachFeedbackService;

    @Test
    @DisplayName("러닝 단일 운동 피드백 SSE 요청을 처리한다")
    void generateRunningWorkoutFeedbackStream() throws Exception {

        doAnswer(invocation -> {
            Consumer<String> chunkConsumer = invocation.getArgument(2);
            chunkConsumer.accept("running feedback");
            return null;
        }).when(coachFeedbackService).getFeedbackStream(any(Long.class), any(CoachFeedbackCommand.class), any());
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);

        MvcResult result = mockMvc.perform(post("/api/v1/coach/feedback/single/stream")
                        .cookie(new Cookie("accessToken", "access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(runningRequestJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        result.getAsyncResult(1_000);

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("running feedback")));

        ArgumentCaptor<Long> memberIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<CoachFeedbackCommand> commandCaptor = ArgumentCaptor.forClass(CoachFeedbackCommand.class);
        verify(coachFeedbackService).getFeedbackStream(memberIdCaptor.capture(), commandCaptor.capture(), any());

        assertThat(memberIdCaptor.getValue()).isEqualTo(member.getId());
        assertThat(commandCaptor.getValue().workOutType().name()).isEqualTo("RUNNING");
        assertThat(commandCaptor.getValue().tier().name()).isEqualTo("AMATEUR");
        assertThat(commandCaptor.getValue().commonCommand().distance()).isEqualTo(8.2);
        assertThat(commandCaptor.getValue().runningCommand().avgPace()).isEqualTo(5.48);
    }

    @Test
    @DisplayName("자전거 단일 운동 피드백 SSE 요청을 처리한다")
    void generateCyclingWorkoutFeedbackStream() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> chunkConsumer = invocation.getArgument(2);
            chunkConsumer.accept("cycling feedback");
            return null;
        }).when(coachFeedbackService).getFeedbackStream(any(Long.class), any(CoachFeedbackCommand.class), any());
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);

        MvcResult result = mockMvc.perform(post("/api/v1/coach/feedback/single/stream")
                        .cookie(new Cookie("accessToken", "access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(cyclingRequestJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        result.getAsyncResult(1_000);

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("cycling feedback")));

        ArgumentCaptor<Long> memberIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<CoachFeedbackCommand> commandCaptor = ArgumentCaptor.forClass(CoachFeedbackCommand.class);
        verify(coachFeedbackService).getFeedbackStream(memberIdCaptor.capture(), commandCaptor.capture(), any());

        assertThat(memberIdCaptor.getValue()).isEqualTo(member.getId());
        assertThat(commandCaptor.getValue().workOutType().name()).isEqualTo("CYCLING");
        assertThat(commandCaptor.getValue().tier().name()).isEqualTo("PRO");
        assertThat(commandCaptor.getValue().commonCommand().distance()).isEqualTo(42.5);
        assertThat(commandCaptor.getValue().cyclingCommand().avgSpeed()).isEqualTo(27.4);
    }

    @Test
    @DisplayName("피드백 방 단일 운동 피드백 SSE 요청을 처리한다")
    void generateRoomSingleWorkoutFeedbackStream() throws Exception {
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);
        UUID roomId = UUID.fromString("6a69e3a4-9d98-83ee-932f-d69cd46770a0");
        doAnswer(invocation -> {
            Consumer<String> chunkConsumer = invocation.getArgument(3);
            chunkConsumer.accept("room feedback");
            return null;
        }).when(coachFeedbackService).generateSingleWorkoutFeedbackStream(any(Long.class), any(UUID.class), any(CoachFeedbackCommand.class), any());

        MvcResult result = mockMvc.perform(post("/api/v1/coach/feedback/rooms/{roomId}/single/stream", roomId)
                        .cookie(new Cookie("accessToken", "access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(runningRequestJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        result.getAsyncResult(1_000);

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("room feedback")));

        ArgumentCaptor<UUID> roomIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(coachFeedbackService).generateSingleWorkoutFeedbackStream(any(Long.class), roomIdCaptor.capture(), any(CoachFeedbackCommand.class), any());
        assertThat(roomIdCaptor.getValue()).isEqualTo(roomId);
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

    private String runningRequestJson() {
        return """
                {
                  "workOutType": "RUNNING",
                  "tier": "AMATEUR",
                  "startedAt": "2026-07-18T07:00:00",
                  "endedAt": "2026-07-18T07:45:00",
                  "distance": 8.2,
                  "elevGain": 120.0,
                  "elevationMax": 85.0,
                  "movingTime": 45,
                  "calories": 530.0,
                  "avgCadence": 172.0,
                  "maxCadence": 188.0,
                  "maxHeartRate": 176.0,
                  "avgHeartRate": 148.0,
                  "avgPace": 5.48,
                  "maxPace": 4.92,
                  "steps": 7600
                }
                """;
    }

    private String cyclingRequestJson() {
        return """
                {
                  "workOutType": "CYCLING",
                  "tier": "PRO",
                  "startedAt": "2026-07-18T09:00:00",
                  "endedAt": "2026-07-18T10:30:00",
                  "distance": 42.5,
                  "elevGain": 650.0,
                  "elevationMax": 240.0,
                  "movingTime": 90,
                  "calories": 920.0,
                  "avgCadence": 88.0,
                  "maxCadence": 104.0,
                  "maxHeartRate": 168.0,
                  "avgHeartRate": 142.0,
                  "avgSpeed": 27.4,
                  "maxSpeed": 44.1,
                  "avgPower": 185.0,
                  "maxPower": 420.0,
                  "ftp": 250.0
                }
                """;
    }
}
