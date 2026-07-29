package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.LogoutCommand;

public record LogoutRequest(
        String refreshToken
) {

    public static LogoutRequest fromCookie(String refreshToken) {
        return new LogoutRequest(refreshToken);
    }

    public LogoutCommand toCommand() {
        return new LogoutCommand(refreshToken);
    }
}
