package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.TermsDetailResult;
import com.example.aisocket.project.domain.TermType;

public record TermsDetailResponse(
        Long termsId,
        TermType type,
        String code,
        String title,
        String version,
        String contentUrl,
        boolean required,
        String content
) {

    public static TermsDetailResponse from(TermsDetailResult result) {
        return new TermsDetailResponse(
                result.termsId(),
                result.type(),
                result.code(),
                result.title(),
                result.version(),
                result.contentUrl(),
                result.required(),
                result.content()
        );
    }
}
