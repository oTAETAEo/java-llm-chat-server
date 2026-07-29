package com.example.aisocket.project.application.dto.command;

import jakarta.validation.constraints.NotBlank;

public record LogoutCommand(
        @NotBlank(message = "액세스 토큰(accessToken)은 필수 값입니다.")
        String accessToken,

        @NotBlank(message = "리프레시 토큰(refreshToken)은 필수 값입니다.")
        String refreshToken
) {
}
