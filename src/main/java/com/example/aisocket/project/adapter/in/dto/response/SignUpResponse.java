package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.SignUpMemberResult;

public record SignUpResponse(
        Long memberId,
        String email,
        String nickname
) {

    public static SignUpResponse from(SignUpMemberResult result) {
        return new SignUpResponse(result.memberId(), result.email(), result.nickname());
    }
}
