package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.LogoutCommand;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {

    public static LogoutRequest fromCookie(String accessToken, String refreshToken) {
        return new LogoutRequest(accessToken, refreshToken);
    }

    public LogoutCommand toCommand() {
        return new LogoutCommand(accessToken, refreshToken);
    }
}
