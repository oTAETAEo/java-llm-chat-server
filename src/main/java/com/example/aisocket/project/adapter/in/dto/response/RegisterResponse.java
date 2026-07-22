package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.RegisterMemberResult;

public record RegisterResponse(
        Long memberId,
        String email,
        String nickname
) {

    public static RegisterResponse from(RegisterMemberResult result) {
        return new RegisterResponse(result.memberId(), result.email(), result.nickname());
    }
}
