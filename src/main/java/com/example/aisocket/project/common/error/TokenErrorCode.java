package com.example.aisocket.project.common.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {

    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID_ACCESS", "액세스 토큰이 아닙니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID_REFRESH", "리프레시 토큰이 아닙니다."),
    BLACKLISTED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_BLACKLISTED_ACCESS", "폐기된 액세스 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_REFRESH_NOT_FOUND", "저장된 리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_FORBIDDEN(HttpStatus.FORBIDDEN, "TOKEN_REFRESH_FORBIDDEN", "리프레시 토큰 소유자가 일치하지 않습니다."),
    REFRESH_TOKEN_UNUSABLE(HttpStatus.UNAUTHORIZED, "TOKEN_REFRESH_UNUSABLE", "사용할 수 없는 리프레시 토큰입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", "인증에 실패했습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", "인증이 필요합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
