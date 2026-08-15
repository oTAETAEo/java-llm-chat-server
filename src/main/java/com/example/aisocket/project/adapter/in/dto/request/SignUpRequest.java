package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;
import com.example.aisocket.project.domain.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "이메일은 필수 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 값입니다.")
        @Size(
                min = Password.MIN_LENGTH,
                max = Password.MAX_LENGTH,
                message = Password.STRONG_PASSWORD_MESSAGE
        )
        @Pattern(
                regexp = Password.STRONG_PASSWORD_REGEX,
                message = Password.STRONG_PASSWORD_MESSAGE
        )
        String password,

        @NotBlank(message = "닉네임은 필수 값입니다.")
        String nickname
) {

    public SignUpMemberCommand toCommand() {
        return new SignUpMemberCommand(email, password, nickname);
    }
}
