package com.example.aisocket.project.application.dto.command;

import jakarta.validation.constraints.NotBlank;

public record LogoutCommand(
        @NotBlank(message = "리프레시 토큰(refreshToken)은 필수 값입니다.")
        String refreshToken
) {
}
