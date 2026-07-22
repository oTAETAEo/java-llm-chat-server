package com.example.aisocket.project.application.service;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.command.RegisterMemberCommand;
import com.example.aisocket.project.application.dto.result.RegisterMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.internal.member.MemberRegisterService;
import com.example.aisocket.project.domain.MemberFixture;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberAuthServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private MemberAuthService memberAuthService;

    @MockitoBean
    private MemberFinderService memberFinderService;

    @MockitoBean
    private MemberRegisterService memberRegisterService;

    @Test
    @DisplayName("회원가입 시 회원 등록 서비스를 호출하고 결과를 반환한다")
    void registerMember() {
        RegisterMemberCommand command = new RegisterMemberCommand("runner@example.com", "raw-password", "runner");
        when(memberRegisterService.register(command)).thenReturn(MemberFixture.builder()
                .id(1L)
                .email(command.email())
                .encodedPassword("hashed-password:raw-password")
                .nickname(command.nickname())
                .build());

        RegisterMemberResult result = memberAuthService.register(command);

        verify(memberFinderService).validateNotExistsByEmail(command.email());
        verify(memberRegisterService).register(command);
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("runner@example.com");
        assertThat(result.nickname()).isEqualTo("runner");
    }

    @Test
    @DisplayName("이미 사용 중인 이메일이면 회원가입에 실패한다")
    void registerMemberWithDuplicatedEmailFails() {
        RegisterMemberCommand command = new RegisterMemberCommand("runner@example.com", "raw-password", "runner");

        doThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."))
                .when(memberFinderService).validateNotExistsByEmail("runner@example.com");

        assertThatThrownBy(() -> memberAuthService.register(command))
                .isInstanceOf(IllegalArgumentException.class);

        verify(memberRegisterService, never()).register(any(RegisterMemberCommand.class));
    }

    @Test
    @DisplayName("회원가입 요청이 없으면 서비스 검증에 실패한다")
    void registerWithoutCommandFails() {
        assertThatThrownBy(() -> memberAuthService.register(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("회원가입 요청 필드가 비어 있으면 서비스 검증에 실패한다")
    void registerWithInvalidCommandFails() {
        RegisterMemberCommand command = new RegisterMemberCommand(" ", "", "");

        assertThatThrownBy(() -> memberAuthService.register(command))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
