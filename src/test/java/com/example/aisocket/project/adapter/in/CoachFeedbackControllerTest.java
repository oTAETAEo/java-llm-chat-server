package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.in.CoachFeedbackService;
import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoachFeedbackController.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
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

    @Test
    @DisplayName("v2 피드백 방 단일 운동 피드백 SSE 요청은 센서 샘플을 함께 처리한다")
    void generateRoomSingleWorkoutFeedbackStreamV2() throws Exception {
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);
        UUID roomId = UUID.fromString("6a69e3a4-9d98-83ee-932f-d69cd46770a0");
        doAnswer(invocation -> {
            Consumer<String> chunkConsumer = invocation.getArgument(4);
            chunkConsumer.accept("room sensor feedback");
            return null;
        }).when(coachFeedbackService).generateSingleWorkoutFeedbackStream(
                any(Long.class),
                any(UUID.class),
                any(CoachFeedbackCommand.class),
                any(CreateWorkoutSensorDataCommand.class),
                any()
        );

        MvcResult result = mockMvc.perform(post("/api/v2/coach/feedback/rooms/{roomId}/single/stream", roomId)
                        .cookie(new Cookie("accessToken", "access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(runningSensorRequestJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        result.getAsyncResult(1_000);

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("room sensor feedback")));

        ArgumentCaptor<CreateWorkoutSensorDataCommand> sensorCommandCaptor = ArgumentCaptor.forClass(CreateWorkoutSensorDataCommand.class);
        verify(coachFeedbackService).generateSingleWorkoutFeedbackStream(
                any(Long.class),
                any(UUID.class),
                any(CoachFeedbackCommand.class),
                sensorCommandCaptor.capture(),
                any()
        );
        assertThat(sensorCommandCaptor.getValue().samplesJson()).contains("\"heartRate\":150", "\"latitude\":37.1");
    }

    @Test
    @DisplayName("피드백 스트림 생성 실패 시 사용자용 에러 이벤트를 응답한다")
    void generateRoomSingleWorkoutFeedbackStreamFailure() throws Exception {
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);
        UUID roomId = UUID.fromString("6a69e3a4-9d98-83ee-932f-d69cd46770a0");
        doThrow(new IllegalStateException("OpenAI timeout"))
                .when(coachFeedbackService)
                .generateSingleWorkoutFeedbackStream(any(Long.class), any(UUID.class), any(CoachFeedbackCommand.class), any());

        MvcResult result = mockMvc.perform(post("/api/v1/coach/feedback/rooms/{roomId}/single/stream", roomId)
                        .cookie(new Cookie("accessToken", "access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(runningRequestJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        result.getAsyncResult(1_000);

        MvcResult dispatched = mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("event:error")))
                .andReturn();

        assertThat(dispatched.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .contains("피드백 생성 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
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

    private String runningSensorRequestJson() {
        return """
                {
                  "workout": {
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
                  },
                  "samples": [
                    {
                      "elapsedSeconds": 0,
                      "distance": 0.0,
                      "latitude": 37.1,
                      "longitude": 127.1,
                      "altitude": 50.0,
                      "heartRate": 150,
                      "cadence": 170,
                      "speed": 12.5,
                      "power": 220
                    }
                  ]
                }
                """;
    }
}
