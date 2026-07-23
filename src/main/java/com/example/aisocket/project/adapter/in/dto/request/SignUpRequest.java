package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;

public record SignUpRequest(
        String email,
        String password,
        String nickname
) {

    public SignUpMemberCommand toCommand() {
        return new SignUpMemberCommand(email, password, nickname);
    }
}
