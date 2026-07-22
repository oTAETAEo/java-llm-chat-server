package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.application.in.MemberAuthService;
import com.example.aisocket.project.application.dto.command.RegisterMemberCommand;
import com.example.aisocket.project.application.dto.result.RegisterMemberResult;
import com.example.aisocket.project.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberAuthService memberAuthService;

    @Test
    @DisplayName("회원가입 요청을 처리한다")
    void register() throws Exception {
        when(memberAuthService.register(any(RegisterMemberCommand.class)))
                .thenReturn(new RegisterMemberResult(1L, "runner@example.com", "runner"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.email").value("runner@example.com"))
                .andExpect(jsonPath("$.nickname").value("runner"));

        ArgumentCaptor<RegisterMemberCommand> commandCaptor = ArgumentCaptor.forClass(RegisterMemberCommand.class);
        verify(memberAuthService).register(commandCaptor.capture());
        assertThat(commandCaptor.getValue().email()).isEqualTo("runner@example.com");
        assertThat(commandCaptor.getValue().rawPassword()).isEqualTo("raw-password");
        assertThat(commandCaptor.getValue().nickname()).isEqualTo("runner");
    }

    private String registerRequestJson() {
        return """
                {
                  "email": "runner@example.com",
                  "password": "raw-password",
                  "nickname": "runner"
                }
                """;
    }
}
