package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.ReissueTokenCommand;

public record ReissueTokenRequest(
        String refreshToken
) {

    public static ReissueTokenRequest fromCookie(String refreshToken) {
        return new ReissueTokenRequest(refreshToken);
    }

    public ReissueTokenCommand toCommand() {
        return new ReissueTokenCommand(refreshToken);
    }
}
