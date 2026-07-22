package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.request.RegisterRequest;
import com.example.aisocket.project.adapter.in.dto.response.RegisterResponse;
import com.example.aisocket.project.application.dto.result.RegisterMemberResult;
import com.example.aisocket.project.application.in.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody RegisterRequest request) {

        RegisterMemberResult result = memberAuthService.register(request.toCommand());

        return RegisterResponse.from(result);
    }
}
