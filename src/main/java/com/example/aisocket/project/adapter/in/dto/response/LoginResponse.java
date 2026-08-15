package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.LoginResult;

public record LoginResponse(
        Long memberId,
        String email,
        String nickname,
        boolean requiresTermsAgreement,
        java.util.List<TermsResponse> missingRequiredTerms
) {

    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.memberId(),
                result.email(),
                result.nickname(),
                result.termsAgreementStatus().requiresTermsAgreement(),
                result.termsAgreementStatus().missingRequiredTerms().stream()
                        .map(TermsResponse::from)
                        .toList()
        );
    }
}
