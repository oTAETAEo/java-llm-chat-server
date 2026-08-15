package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.TermsResult;
import com.example.aisocket.project.domain.TermType;

public record TermsResponse(
        Long termsId,
        TermType type,
        String code,
        String title,
        String version,
        String contentUrl,
        boolean required
) {

    public static TermsResponse from(TermsResult result) {
        return new TermsResponse(
                result.termsId(),
                result.type(),
                result.code(),
                result.title(),
                result.version(),
                result.contentUrl(),
                result.required()
        );
    }
}
