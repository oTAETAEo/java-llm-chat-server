package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.LoginCommand;

public record LoginRequest(
        String email,
        String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
