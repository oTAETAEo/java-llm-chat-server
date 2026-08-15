package com.example.aisocket.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "terms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Terms extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TermType type;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 50)
    private String version;

    @Column(nullable = false)
    private String contentUrl;

    @Column(columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private boolean required;

    @Column(nullable = false)
    private boolean active;

    public static Terms of(
            Long id,
            TermType type,
            String code,
            String title,
            String version,
            String contentUrl,
            String content,
            boolean required,
            boolean active
    ) {
        Terms terms = new Terms(type, code, title, version, contentUrl, content, required, active);
        terms.id = id;
        return terms;
    }

    private Terms(
            TermType type,
            String code,
            String title,
            String version,
            String contentUrl,
            String content,
            boolean required,
            boolean active
    ) {
        this.type = type;
        this.code = code;
        this.title = title;
        this.version = version;
        this.contentUrl = contentUrl;
        this.content = content;
        this.required = required;
        this.active = active;

        validate();
    }

    private void validate() {
        if (type == null) {
            throw new IllegalArgumentException("약관 유형(type)은 필수 값입니다.");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("약관 코드(code)는 필수 값입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("약관 제목(title)은 필수 값입니다.");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("약관 버전(version)은 필수 값입니다.");
        }
        if (contentUrl == null || contentUrl.isBlank()) {
            throw new IllegalArgumentException("약관 URL(contentUrl)은 필수 값입니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("약관 본문(content)은 필수 값입니다.");
        }
    }
}
