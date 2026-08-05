package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.security.JwtAuthenticationFilter;
import com.example.aisocket.project.application.dto.result.FitWorkoutPreviewResult;
import com.example.aisocket.project.application.in.FitWorkoutUploadService;
import com.example.aisocket.project.application.internal.token.AccessTokenBlacklistService;
import com.example.aisocket.project.application.internal.token.JwtTokenClaims;
import com.example.aisocket.project.application.internal.token.JwtTokenValidator;
import com.example.aisocket.project.application.out.MemberRepository;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.WorkOutType;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FitWorkoutUploadController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class FitWorkoutUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenValidator jwtTokenValidator;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private AccessTokenBlacklistService accessTokenBlacklistService;

    @MockitoBean
    private FitWorkoutUploadService fitWorkoutUploadService;

    @Test
    @DisplayName("단일 FIT 파일 업로드를 파싱 미리보기 응답으로 처리한다")
    void upload() throws Exception {
        Member member = authenticatedMember();
        givenAuthenticatedMember(member);
        MockMultipartFile file = new MockMultipartFile("file", "activity.fit", "application/octet-stream", new byte[]{1, 2, 3});

        given(fitWorkoutUploadService.upload(eq(member.getId()), eq(AthleteTier.PRO), any()))
                .willReturn(result());

        mockMvc.perform(multipart("/api/v1/workouts/fit")
                        .file(file)
                        .param("tier", "PRO")
                        .cookie(new Cookie("accessToken", "access-token"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workOutType").value("RUNNING"))
                .andExpect(jsonPath("$.tier").value("PRO"))
                .andExpect(jsonPath("$.distance").value(10.0))
                .andExpect(jsonPath("$.ftp").doesNotExist())
                .andExpect(jsonPath("$.samples[0].latitude").value(37.1))
                .andExpect(jsonPath("$.samples[0].heartRate").value(150));

        ArgumentCaptor<MockMultipartFile> fileCaptor = ArgumentCaptor.forClass(MockMultipartFile.class);
        verify(fitWorkoutUploadService).upload(eq(member.getId()), eq(AthleteTier.PRO), fileCaptor.capture());
        assertThat(fileCaptor.getValue().getOriginalFilename()).isEqualTo("activity.fit");
    }

    private FitWorkoutPreviewResult result() {
        return new FitWorkoutPreviewResult(
                WorkOutType.RUNNING,
                AthleteTier.PRO,
                LocalDateTime.parse("2026-08-05T00:00:00"),
                LocalDateTime.parse("2026-08-05T00:50:00"),
                10.0,
                100.0,
                250.0,
                3000,
                650.0,
                170.0,
                190.0,
                180.0,
                150.0,
                null,
                null,
                null,
                null,
                null,
                300.0,
                null,
                9000,
                List.of(new FitWorkoutPreviewResult.FitSensorSampleResult(
                        0,
                        0.0,
                        37.1,
                        127.1,
                        50.0,
                        150,
                        170,
                        12.5,
                        null
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
