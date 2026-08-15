package com.example.aisocket.project.application.dto.result;

import com.example.aisocket.project.domain.TermType;
import com.example.aisocket.project.domain.Terms;

public record TermsResult(
        Long termsId,
        TermType type,
        String code,
        String title,
        String version,
        String contentUrl,
        boolean required
) {

    public static TermsResult from(Terms terms) {
        return new TermsResult(
                terms.getId(),
                terms.getType(),
                terms.getCode(),
                terms.getTitle(),
                terms.getVersion(),
                terms.getContentUrl(),
                terms.isRequired()
        );
    }
}
