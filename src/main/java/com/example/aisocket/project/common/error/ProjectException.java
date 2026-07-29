package com.example.aisocket.project.common.error;

public class ProjectException extends RuntimeException {

    private final ErrorCode errorCode;

    public ProjectException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public ProjectException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }
}
