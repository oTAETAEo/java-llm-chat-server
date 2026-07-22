package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.RegisterMemberCommand;

public record RegisterRequest(
        String email,
        String password,
        String nickname
) {

    public RegisterMemberCommand toCommand() {
        return new RegisterMemberCommand(email, password, nickname);
    }
}
