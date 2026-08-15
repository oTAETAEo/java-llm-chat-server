package com.example.aisocket.project.adapter.in.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TermsAgreementRequest(
        @NotNull(message = "동의 약관 ID 목록(agreedTermsIds)은 필수 값입니다.")
        List<Long> agreedTermsIds
) {
}
