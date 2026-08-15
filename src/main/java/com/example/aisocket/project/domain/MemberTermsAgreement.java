package com.example.aisocket.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "member_terms_agreements",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_terms_agreement_member_terms",
                columnNames = {"member_id", "terms_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTermsAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    @Column(nullable = false, length = 100)
    private String termsCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TermType termsType;

    @Column
    private String termsTitle;

    @Column(nullable = false, length = 50)
    private String termsVersion;

    @Column
    private String termsContentUrl;

    @Column(columnDefinition = "text")
    private String termsContent;

    @Column
    private Boolean termsRequired;

    public static MemberTermsAgreement agree(Member member, Terms terms) {
        return new MemberTermsAgreement(member, terms);
    }

    private MemberTermsAgreement(Member member, Terms terms) {
        this.member = member;
        this.terms = terms;
        this.termsCode = terms == null ? null : terms.getCode();
        this.termsType = terms == null ? null : terms.getType();
        this.termsTitle = terms == null ? null : terms.getTitle();
        this.termsVersion = terms == null ? null : terms.getVersion();
        this.termsContentUrl = terms == null ? null : terms.getContentUrl();
        this.termsContent = terms == null ? null : terms.getContent();
        this.termsRequired = terms == null ? null : terms.isRequired();

        validate();
    }

    private void validate() {
        if (member == null || member.getId() == null) {
            throw new IllegalArgumentException("약관 동의 회원(member)은 필수 값입니다.");
        }
        if (terms == null || terms.getId() == null) {
            throw new IllegalArgumentException("동의 약관(terms)은 필수 값입니다.");
        }
        if (termsCode == null || termsCode.isBlank()) {
            throw new IllegalArgumentException("동의 약관 코드(termsCode)는 필수 값입니다.");
        }
        if (termsVersion == null || termsVersion.isBlank()) {
            throw new IllegalArgumentException("동의 약관 버전(termsVersion)은 필수 값입니다.");
        }
        if (termsType == null) {
            throw new IllegalArgumentException("동의 약관 유형(termsType)은 필수 값입니다.");
        }
        if (termsTitle == null || termsTitle.isBlank()) {
            throw new IllegalArgumentException("동의 약관 제목(termsTitle)은 필수 값입니다.");
        }
        if (termsContentUrl == null || termsContentUrl.isBlank()) {
            throw new IllegalArgumentException("동의 약관 URL(termsContentUrl)은 필수 값입니다.");
        }
        if (termsContent == null || termsContent.isBlank()) {
            throw new IllegalArgumentException("동의 약관 본문(termsContent)은 필수 값입니다.");
        }
        if (termsRequired == null) {
            throw new IllegalArgumentException("동의 약관 필수 여부(termsRequired)는 필수 값입니다.");
        }
    }
}
