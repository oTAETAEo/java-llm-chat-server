package com.example.aisocket.project.application.internal.terms;

import com.example.aisocket.project.application.dto.result.TermsAgreementStatusResult;
import com.example.aisocket.project.application.internal.member.MemberFinderService;
import com.example.aisocket.project.application.out.MemberTermsAgreementRepository;
import com.example.aisocket.project.application.out.TermsRepository;
import com.example.aisocket.project.common.error.MemberErrorCode;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.MemberTermsAgreement;
import com.example.aisocket.project.domain.TermType;
import com.example.aisocket.project.domain.Terms;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class TermsAgreementStatusServiceImplTest {

    private final MemberFinderService memberFinderService = mock(MemberFinderService.class);
    private final TermsRepository termsRepository = mock(TermsRepository.class);
    private final MemberTermsAgreementRepository memberTermsAgreementRepository = mock(MemberTermsAgreementRepository.class);
    private final TermsAgreementStatusServiceImpl service = new TermsAgreementStatusServiceImpl(
            memberFinderService,
            termsRepository,
            memberTermsAgreementRepository
    );

    @Test
    @DisplayName("누락된 활성 필수 약관이 있으면 재동의 필요 상태를 반환한다")
    void findStatusRequiresTermsAgreement() {
        given(memberTermsAgreementRepository.findAgreedActiveTermsIdsByMemberId(1L))
                .willReturn(Set.of(1L));
        given(termsRepository.findActiveRequiredTerms())
                .willReturn(List.of(requiredTerms(1L), requiredTerms(2L)));

        TermsAgreementStatusResult result = service.findStatus(1L);

        assertThat(result.requiresTermsAgreement()).isTrue();
        assertThat(result.missingRequiredTerms())
                .extracting("termsId")
                .containsExactly(2L);
    }

    @Test
    @DisplayName("기존 동의와 추가 동의를 합쳐 필수 약관을 모두 충족하면 누락 약관만 저장한다")
    void agreeStoresOnlyNewTerms() {
        given(memberFinderService.findById(1L)).willReturn(MemberFixture.builder().id(1L).build());
        given(memberTermsAgreementRepository.findAgreedActiveTermsIdsByMemberId(1L))
                .willReturn(Set.of(1L))
                .willReturn(Set.of(1L, 2L));
        given(termsRepository.findActiveRequiredTerms())
                .willReturn(List.of(requiredTerms(1L), requiredTerms(2L)));
        given(termsRepository.findActiveByIds(Set.of(1L, 2L)))
                .willReturn(List.of(requiredTerms(1L), requiredTerms(2L)));

        TermsAgreementStatusResult result = service.agree(1L, List.of(1L, 2L));

        assertThat(result.requiresTermsAgreement()).isFalse();
        verify(memberTermsAgreementRepository).save(any(MemberTermsAgreement.class));
    }

    @Test
    @DisplayName("추가 동의 후에도 필수 약관이 누락되면 저장하지 않고 실패한다")
    void agreeWithoutRequiredTermsFails() {
        given(memberFinderService.findById(1L)).willReturn(MemberFixture.builder().id(1L).build());
        given(memberTermsAgreementRepository.findAgreedActiveTermsIdsByMemberId(1L))
                .willReturn(Set.of(1L));
        given(termsRepository.findActiveRequiredTerms())
                .willReturn(List.of(requiredTerms(1L), requiredTerms(2L), requiredTerms(3L)));

        assertThatThrownBy(() -> service.agree(1L, List.of(2L)))
                .isInstanceOf(ProjectException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.REQUIRED_TERMS_NOT_AGREED);

        verify(memberTermsAgreementRepository, never()).save(any(MemberTermsAgreement.class));
    }

    private Terms requiredTerms(Long id) {
        return Terms.of(
                id,
                TermType.TERMS_OF_SERVICE,
                "required-terms-" + id,
                "필수 약관 " + id,
                "2026.08.15",
                "/terms/" + id,
                "약관 본문 " + id,
                true,
                true
        );
    }
}
