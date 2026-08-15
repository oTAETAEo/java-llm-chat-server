package com.example.aisocket.project.common.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TermsErrorCode implements ErrorCode {

    TERMS_NOT_FOUND(HttpStatus.NOT_FOUND, "TERMS_NOT_FOUND", "약관을 찾을 수 없습니다.");

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
