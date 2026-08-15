package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.ReissueTokenResult;

public record ReissueTokenResponse(
        Long memberId,
        String email,
        String nickname,
        boolean requiresTermsAgreement,
        java.util.List<TermsResponse> missingRequiredTerms
) {

    public static ReissueTokenResponse from(ReissueTokenResult result) {
        return new ReissueTokenResponse(
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
