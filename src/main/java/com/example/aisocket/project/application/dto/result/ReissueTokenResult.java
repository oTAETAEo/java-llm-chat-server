package com.example.aisocket.project.application.dto.result;

import java.time.Instant;

public record ReissueTokenResult(
        Long memberId,
        String email,
        String nickname,
        TermsAgreementStatusResult termsAgreementStatus,
        String accessToken,
        Instant accessTokenExpiresAt
) {
}
