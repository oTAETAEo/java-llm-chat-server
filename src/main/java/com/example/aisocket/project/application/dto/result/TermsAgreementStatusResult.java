package com.example.aisocket.project.application.dto.result;

import java.util.List;

public record TermsAgreementStatusResult(
        boolean requiresTermsAgreement,
        List<TermsResult> missingRequiredTerms
) {
}
