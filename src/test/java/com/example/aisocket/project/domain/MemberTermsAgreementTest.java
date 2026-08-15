package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTermsAgreementTest {

    @Test
    @DisplayName("약관 동의 이력은 동의 당시 약관 메타데이터를 스냅샷으로 저장한다")
    void agreeStoresTermsSnapshot() {
        Member member = MemberFixture.builder().id(1L).build();
        Terms terms = Terms.of(
                3L,
                TermType.SENSITIVE_INFORMATION,
                "sensitive-health-data-kr-v1",
                "건강 관련 운동 데이터 처리 동의",
                "2026.08.15",
                "/privacy#sensitive-information",
                "건강 관련 운동 데이터를 처리합니다.",
                true,
                true
        );

        MemberTermsAgreement agreement = MemberTermsAgreement.agree(member, terms);

        assertThat(agreement.getMember()).isSameAs(member);
        assertThat(agreement.getTerms()).isSameAs(terms);
        assertThat(agreement.getTermsCode()).isEqualTo("sensitive-health-data-kr-v1");
        assertThat(agreement.getTermsType()).isEqualTo(TermType.SENSITIVE_INFORMATION);
        assertThat(agreement.getTermsTitle()).isEqualTo("건강 관련 운동 데이터 처리 동의");
        assertThat(agreement.getTermsVersion()).isEqualTo("2026.08.15");
        assertThat(agreement.getTermsContentUrl()).isEqualTo("/privacy#sensitive-information");
        assertThat(agreement.getTermsContent()).isEqualTo("건강 관련 운동 데이터를 처리합니다.");
        assertThat(agreement.getTermsRequired()).isTrue();
    }

    @Test
    @DisplayName("저장된 회원이 아니면 약관 동의 이력을 만들 수 없다")
    void agreeWithoutSavedMemberFails() {
        Member member = MemberFixture.builder().buildNew();
        Terms terms = Terms.of(
                1L,
                TermType.TERMS_OF_SERVICE,
                "terms-of-service-kr-v1",
                "이용약관",
                "2026.08.15",
                "/terms",
                "서비스 이용약관입니다.",
                true,
                true
        );

        assertThatThrownBy(() -> MemberTermsAgreement.agree(member, terms))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원");
    }
}
