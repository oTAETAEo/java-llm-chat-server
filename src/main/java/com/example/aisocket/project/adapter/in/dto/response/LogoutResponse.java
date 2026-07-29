package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.LogoutResult;

public record LogoutResponse(
        Long memberId
) {

    public static LogoutResponse from(LogoutResult result) {
        return new LogoutResponse(result.memberId());
    }
}
