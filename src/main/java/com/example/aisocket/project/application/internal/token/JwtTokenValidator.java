package com.example.aisocket.project.application.internal.token;

public interface JwtTokenValidator {

    JwtTokenClaims validate(String token);
}
