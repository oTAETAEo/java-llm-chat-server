package com.example.aisocket.project.common.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WorkoutErrorCode implements ErrorCode {

    UNSUPPORTED_WORKOUT_TYPE(HttpStatus.BAD_REQUEST, "WORKOUT_UNSUPPORTED_TYPE", "지원하지 않는 운동 타입입니다."),
    INVALID_WORKOUT_DATA(HttpStatus.BAD_REQUEST, "WORKOUT_INVALID_DATA", "운동 데이터가 올바르지 않습니다."),
    WORKOUT_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKOUT_NOT_FOUND", "운동 기록을 찾을 수 없습니다.");

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
