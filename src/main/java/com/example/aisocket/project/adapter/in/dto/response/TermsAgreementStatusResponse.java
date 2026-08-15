package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.TermsAgreementStatusResult;

import java.util.List;

public record TermsAgreementStatusResponse(
        boolean requiresTermsAgreement,
        List<TermsResponse> missingRequiredTerms
) {

    public static TermsAgreementStatusResponse from(TermsAgreementStatusResult result) {
        return new TermsAgreementStatusResponse(
                result.requiresTermsAgreement(),
                result.missingRequiredTerms().stream()
                        .map(TermsResponse::from)
                        .toList()
        );
    }
}
