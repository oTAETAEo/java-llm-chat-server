package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.TermType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TermsDataInitializer implements ApplicationRunner {

    private static final String VERSION = "2026.08.15";

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        removeOutdatedEnumCheckConstraints();

        upsertTerms(
                1L,
                TermType.TERMS_OF_SERVICE,
                "terms-of-service-kr-v1",
                "이용약관",
                VERSION,
                "/terms",
                TERMS_OF_SERVICE_CONTENT,
                true,
                true
        );
        upsertTerms(
                2L,
                TermType.PRIVACY_POLICY,
                "privacy-policy-kr-v1",
                "개인정보 처리방침",
                VERSION,
                "/privacy",
                PRIVACY_POLICY_CONTENT,
                true,
                true
        );
        upsertTerms(
                3L,
                TermType.SENSITIVE_INFORMATION,
                "sensitive-health-data-kr-v1",
                "건강 관련 운동 데이터 처리 동의",
                VERSION,
                "/privacy#sensitive-information",
                SENSITIVE_INFORMATION_CONTENT,
                true,
                true
        );
        upsertTerms(
                4L,
                TermType.MARKETING,
                "marketing-consent-kr-v1",
                "마케팅 정보 수신 동의",
                VERSION,
                "/privacy#marketing-consent",
                MARKETING_CONTENT,
                false,
                true
        );

        jdbcTemplate.execute("""
                SELECT setval(
                    pg_get_serial_sequence('terms', 'id'),
                    GREATEST((SELECT COALESCE(MAX(id), 1) FROM terms), 1)
                )
                """);
    }

    private void removeOutdatedEnumCheckConstraints() {
        jdbcTemplate.execute("ALTER TABLE terms DROP CONSTRAINT IF EXISTS terms_type_check");
        jdbcTemplate.execute("ALTER TABLE member_terms_agreements DROP CONSTRAINT IF EXISTS member_terms_agreements_terms_type_check");
    }

    private void upsertTerms(
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
        jdbcTemplate.update(
                """
                        INSERT INTO terms (
                            id,
                            type,
                            code,
                            title,
                            version,
                            content_url,
                            content,
                            required,
                            active,
                            created_at,
                            updated_at
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
                        ON CONFLICT (id) DO UPDATE
                        SET type = EXCLUDED.type,
                            code = EXCLUDED.code,
                            title = EXCLUDED.title,
                            version = EXCLUDED.version,
                            content_url = EXCLUDED.content_url,
                            content = EXCLUDED.content,
                            required = EXCLUDED.required,
                            active = EXCLUDED.active,
                            updated_at = NOW()
                        """,
                id,
                type.name(),
                code,
                title,
                version,
                contentUrl,
                content,
                required,
                active
        );
    }

    private static final String TERMS_OF_SERVICE_CONTENT = """
            # Workout AI Coach 이용약관

            본 약관은 최태현 개인(이하 "운영자")이 activity-coaching.com 및 api.activity-coaching.com을 통해 제공하는 Workout AI Coach 서비스의 이용 조건과 운영자 및 회원의 권리·의무를 정합니다.

            - 시행일: 2026.08.15
            - 최종 개정일: 2026.08.15

            ## 제1조 목적

            본 약관은 운영자가 제공하는 운동 기록 저장, FIT 파일 분석, AI 운동 피드백, 대시보드 서비스의 이용과 관련하여 운영자와 회원 사이의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.

            ## 제2조 정의

            1. "서비스"란 Workout AI Coach가 제공하는 웹 기반 운동 기록 관리 및 AI 피드백 서비스를 말합니다.
            2. "회원"이란 본 약관과 개인정보 처리방침에 동의하고 계정을 생성한 이용자를 말합니다.
            3. "운동 기록"이란 회원이 직접 입력하거나 FIT 파일 등을 통해 업로드한 러닝·사이클링 운동 데이터와 센서 데이터를 말합니다.
            4. "AI 피드백"이란 회원의 운동 기록을 바탕으로 생성되는 자동화된 분석 및 코칭 참고 정보를 말합니다.

            ## 제3조 약관의 게시와 개정

            운영자는 본 약관의 내용을 회원이 쉽게 확인할 수 있도록 서비스 화면 또는 연결 페이지에 게시합니다. 운영자는 관련 법령을 위반하지 않는 범위에서 본 약관을 개정할 수 있습니다. 약관을 개정하는 경우 적용일자와 개정 사유를 명시하여 적용일 7일 전부터 공지합니다. 회원에게 불리한 중요한 변경의 경우 30일 이상의 사전 유예기간을 두고 공지합니다.

            ## 제4조 회원가입

            회원가입은 이용자가 이메일, 비밀번호, 닉네임 등 필요한 정보를 입력하고 본 약관 및 개인정보 처리방침 등 필수 약관에 동의한 뒤 운영자가 이를 승낙함으로써 완료됩니다. 운영자는 허위 정보 입력, 타인 정보 도용, 서비스 운영 방해 목적, 법령 또는 약관 위반 이력이 있는 경우 가입 신청을 거절하거나 사후에 이용계약을 해지할 수 있습니다.

            ## 제5조 계정 관리

            회원은 본인의 이메일, 비밀번호 등 계정 정보를 안전하게 관리해야 합니다. 회원의 관리 소홀로 발생한 손해에 대해서는 운영자의 고의 또는 중대한 과실이 없는 한 회원이 책임을 부담합니다.

            ## 제6조 서비스의 제공

            운영자는 러닝·사이클링 운동 기록 입력 및 저장, FIT 파일 업로드 및 센서 데이터 추출, 운동 기록 기반 AI 피드백 생성, 운동 기록 이력 및 대시보드 제공, 기타 운동 분석 관련 기능을 제공합니다. AI 피드백은 운동 개선을 위한 참고 정보이며 의료, 진단, 치료, 전문 코칭, 경기 결과 보장을 의미하지 않습니다.

            ## 제7조 서비스의 변경 및 중단

            운영자는 운영상 또는 기술상 필요한 경우 서비스의 전부 또는 일부를 변경하거나 일시 중단할 수 있습니다. 서버, 네트워크, 데이터베이스, 보안 사고 대응, 긴급 점검, 클라우드 제공자 장애, 외부 AI API 장애 등으로 사전 공지 없이 서비스가 일시 중단될 수 있습니다.

            ## 제8조 회원의 의무

            회원은 타인의 계정 또는 개인정보 도용, 허위 운동 기록 또는 비정상 파일 업로드, 서비스 보안 취약점 악용, 자동화된 대량 요청, 운영자 또는 제3자의 권리 침해, 법령 또는 공서양속에 반하는 행위를 해서는 안 됩니다.

            ## 제9조 개인정보 보호

            운영자는 「개인정보 보호법」 등 관련 법령이 정하는 바에 따라 회원의 개인정보를 보호하기 위해 노력합니다. 개인정보의 처리에 관한 자세한 사항은 별도 개인정보 처리방침에서 정합니다.

            ## 제10조 마케팅 정보 수신

            운영자는 별도 동의한 회원에게 서비스 업데이트, 이벤트, 혜택 안내 등 마케팅 정보를 이메일 등으로 발송할 수 있습니다. 회원은 언제든지 수신을 거부할 수 있으며, 수신 거부로 인해 기본 서비스 이용에 제한을 받지 않습니다.

            ## 제11조 저작권 및 데이터 권리

            서비스 화면, 소프트웨어, 디자인, 문서 등 운영자가 제작한 콘텐츠의 지식재산권은 운영자에게 귀속됩니다. 회원이 업로드한 운동 기록과 파일에 대한 권리는 회원에게 있습니다. 다만 회원은 서비스 제공, 저장, 분석, AI 피드백 생성, 오류 수정 및 서비스 개선을 위해 필요한 범위에서 운영자가 해당 데이터를 이용할 수 있도록 허락합니다.

            ## 제12조 이용 제한 및 계약 해지

            회원은 언제든지 서비스 내 기능 또는 문의를 통해 탈퇴를 요청할 수 있습니다. 운영자는 회원이 본 약관을 위반하거나 서비스 운영을 방해하는 경우 사전 통지 후 이용을 제한하거나 계약을 해지할 수 있습니다.

            ## 제13조 책임 제한

            운영자는 천재지변, 클라우드 장애, 외부 API 장애, 회원의 귀책 사유 등 운영자의 합리적 통제 범위를 벗어난 사유로 서비스를 제공할 수 없는 경우 책임을 부담하지 않습니다. 운영자는 AI 피드백의 정확성, 완전성, 특정 운동 성과 달성을 보장하지 않습니다.

            ## 제14조 유료 서비스 및 환불

            현재 서비스는 자체 결제 기능을 제공하지 않습니다. 향후 유료 서비스, 구독, 결제, 환불 기능을 제공하는 경우 결제 수단, 청약철회, 환불 기준, 자동 갱신 여부를 별도 약관 또는 본 약관 개정을 통해 고지합니다.

            ## 제15조 분쟁 해결

            운영자와 회원은 서비스 이용과 관련하여 분쟁이 발생한 경우 성실히 협의하여 해결합니다. 협의로 해결되지 않는 경우 관련 법령에 따른 분쟁조정기관 또는 관할 법원에 해결을 요청할 수 있습니다.

            ## 제16조 준거법 및 관할

            본 약관은 대한민국 법령에 따라 해석됩니다. 운영자와 회원 사이에 발생한 분쟁에 관한 소송은 민사소송법 등 관련 법령에서 정한 관할 법원에 제기합니다.

            ## 부칙

            본 약관은 2026.08.15부터 시행합니다.
            """;

    private static final String PRIVACY_POLICY_CONTENT = """
            # Workout AI Coach 개인정보 처리방침

            최태현 개인(이하 "운영자")은 「개인정보 보호법」 제30조에 따라 정보주체의 개인정보를 보호하고 관련 고충을 신속하고 원활하게 처리하기 위하여 다음과 같이 개인정보 처리방침을 수립·공개합니다.

            - 서비스명: Workout AI Coach
            - 서비스 도메인: activity-coaching.com
            - API 도메인: api.activity-coaching.com
            - 시행일: 2026.08.15
            - 최종 개정일: 2026.08.15

            ## 제1조 개인정보의 처리 목적

            운영자는 회원 관리, 운동 기록 저장, AI 운동 피드백 제공, 대시보드 제공, 고객 문의 대응, 서비스 개선, 마케팅 정보 안내 목적을 위해 개인정보를 처리합니다.

            | 처리 목적 | 상세 내용 |
            |----------|----------|
            | 회원 관리 | 회원가입 의사 확인, 회원 식별, 로그인, 계정 보안, 부정 이용 방지 |
            | 운동 기록 저장 | 러닝·사이클링 운동 기록, FIT 파일, 센서 데이터를 저장하고 사용자별 이력을 관리 |
            | AI 운동 피드백 제공 | 운동 기록과 센서 데이터를 분석하여 AI 기반 코칭 피드백 제공 |
            | 대시보드 제공 | 운동 횟수, 거리, 심박, 파워, 페이스 등 통계와 시각화 제공 |
            | 고객 문의 대응 | 서비스 문의, 개인정보 권리 행사, 불만 및 분쟁 처리 |
            | 서비스 개선 | 오류 분석, 서비스 이용 통계, 성능 개선 및 신규 기능 개발 |
            | 마케팅 정보 안내 | 별도 동의한 회원에게 서비스 업데이트, 이벤트, 혜택 안내 |

            ## 제2조 처리하는 개인정보의 항목

            ### 필수 항목

            - 이메일: 로그인, 계정 식별, 문의 응대
            - 비밀번호: 자체 회원가입 인증. 비밀번호는 해시 처리되어 저장됩니다.
            - 닉네임: 서비스 화면 표시, 사용자 식별
            - 운동 기록: 운동 유형, 시작·종료 시각, 거리, 이동 시간, 칼로리, 고도, 페이스, 속도, 파워, 케이던스 등
            - 건강 관련 운동 데이터: 심박수, 운동 강도 등 FIT 파일 또는 직접 입력으로 제공되는 센서 데이터

            ### 선택 항목

            - FIT 파일: 운동 기록 자동 입력 및 AI 피드백 정확도 향상
            - 마케팅 수신 동의 여부: 이벤트와 서비스 업데이트 안내

            ### 자동 수집 항목

            서비스 이용 과정에서 IP 주소, 쿠키, 접속 일시, 서비스 이용 기록, 브라우저와 기기 정보, 오류 로그가 자동으로 생성되어 수집될 수 있습니다.

            ### 민감정보 처리

            <span id="sensitive-information" />

            운동 서비스 특성상 심박수 등 건강 관련 정보가 포함될 수 있습니다. 운영자는 건강 관련 운동 데이터를 운동 분석과 AI 피드백 제공 목적으로만 처리하며, 회원가입 또는 운동 데이터 업로드 과정에서 별도 동의를 받습니다.

            ### 고유식별정보

            운영자는 주민등록번호, 여권번호, 운전면허번호, 외국인등록번호를 수집하지 않습니다.

            ## 제3조 개인정보의 처리 및 보유 기간

            | 처리 업무 | 보유 기간 | 법령 근거 또는 기준 |
            |----------|----------|-------------------|
            | 회원 계정 정보 | 회원 탈퇴 시까지 | 회원 서비스 제공 계약 |
            | 운동 기록 및 AI 피드백 이력 | 회원 탈퇴 또는 삭제 요청 시까지 | 운동 기록 저장 및 피드백 제공 |
            | 마케팅 수신 동의 이력 | 동의 철회 또는 회원 탈퇴 시까지 | 마케팅 동의 관리 |
            | 서비스 이용 로그 | 수집일로부터 최대 1년 | 보안, 장애 대응, 부정 이용 방지 |
            | 소비자 불만 또는 분쟁 처리 기록 | 3년 | 전자상거래 등에서의 소비자보호에 관한 법률 |
            | 계약 또는 청약철회 관련 기록 | 5년 | 전자상거래 등에서의 소비자보호에 관한 법률 |

            ## 제4조 개인정보의 제3자 제공

            운영자는 정보주체의 개인정보를 제1조에서 명시한 범위 내에서만 처리하며, 정보주체의 동의 또는 법률의 특별한 규정이 있는 경우를 제외하고 개인정보를 제3자에게 제공하지 않습니다.

            ## 제5조 개인정보 처리의 위탁

            | 수탁자 | 위탁 업무 | 위탁 기간 | 국가 |
            |-------|----------|----------|------|
            | Amazon Web Services, Inc. | 서버, 데이터베이스, 스토리지 등 인프라 운영 | 회원 탈퇴 또는 위탁계약 종료 시까지 | 대한민국 또는 미국 등 사용 리전 |
            | Vercel Inc. | 프론트엔드 배포, CDN, 로그 처리 | 회원 탈퇴 또는 위탁계약 종료 시까지 | 미국 |
            | OpenAI, L.L.C. | AI 운동 피드백 생성을 위한 입력 데이터 처리 | API 처리 목적 달성 시 또는 계약상 보관 기간까지 | 미국 |

            운영자는 위탁계약 체결 시 위탁업무 수행 목적 외 개인정보 처리 금지, 기술적·관리적 보호조치, 재위탁 제한, 수탁자 관리·감독, 손해배상 책임에 관한 사항을 문서 또는 계약으로 정합니다.

            ## 제6조 개인정보의 파기 절차 및 방법

            운영자는 개인정보 보유기간의 경과, 처리 목적 달성, 회원 탈퇴 또는 삭제 요청 등 개인정보가 불필요하게 되었을 때 지체 없이 해당 개인정보를 파기합니다. 전자적 파일은 복구 및 재생이 불가능한 방법으로 영구 삭제하고, 출력물은 분쇄 또는 소각합니다.

            ## 제7조 정보주체와 법정대리인의 권리·의무 및 행사 방법

            정보주체는 운영자에게 언제든지 개인정보 열람, 정정, 삭제, 처리정지, 동의 철회, 전송 요구의 권리를 행사할 수 있습니다. 권리 행사는 전자우편, 전화, 서비스 내 문의 수단으로 요청할 수 있으며, 운영자는 본인 확인 후 지체 없이 조치합니다.

            - 이메일: ggg7515@naver.com
            - 전화: 010-9799-7515
            - 우편: 미정
            - 온라인: 서비스 내 계정 또는 문의 기능

            본 서비스는 만 14세 미만 아동을 대상으로 하지 않습니다.

            ## 제8조 개인정보 자동 수집 장치의 설치·운영 및 거부

            운영자는 로그인 유지, 보안, 서비스 이용 통계, 사용자 경험 개선을 위해 쿠키를 사용할 수 있습니다. 필수 쿠키는 서비스 제공에 필요하며, 분석·마케팅 쿠키는 선택 동의를 받은 경우에만 사용합니다.

            ### 쿠키 거부 방법

            - Chrome: 설정 > 개인정보 및 보안 > 쿠키 및 기타 사이트 데이터
            - Safari: 환경설정 > 개인정보 보호 > 쿠키 및 웹사이트 데이터 관리
            - Edge: 설정 > 쿠키 및 사이트 권한 > 쿠키 및 사이트 데이터 관리
            - Firefox: 설정 > 개인정보 및 보안 > 쿠키와 사이트 데이터
            - iOS: 설정 > 개인정보 보호 및 보안 > 추적 > 앱이 추적 요청하지 않도록 허용
            - Android: 설정 > 개인정보 보호 > 광고 > 광고 ID 재설정

            운영자는 현재 맞춤형 광고 목적의 Meta Pixel, Google Ads 리마케팅 등 행태정보 기반 광고 도구를 사용하지 않습니다.

            ## 제9조 개인정보의 안전성 확보 조치

            운영자는 개인정보 취급자 최소화, 내부 접근 권한 관리, 위탁업체 관리, 비밀번호 해시 저장, HTTPS 통신, 인증 토큰 HttpOnly 쿠키 사용, 접근 권한 통제, 로그 관리 등 관리적·기술적·물리적 보호조치를 적용합니다.

            ## 제10조 개인정보 보호책임자 및 고충처리 부서

            ### 개인정보 보호책임자

            - 성명: 최태현
            - 직책: 운영자
            - 연락처: 010-9799-7515
            - 이메일: ggg7515@naver.com

            개인정보 보호책임자 정보는 현재 임시 지정 상태이며, 실제 사업자 등록 및 운영 체계 확정 시 최신 정보로 갱신합니다.

            ### 고충처리 담당

            - 담당: 운영자
            - 연락처: 010-9799-7515
            - 이메일: ggg7515@naver.com
            - 운영시간: 평일 영업시간 기준으로 순차 응대

            ## 제11조 자동화된 결정에 관한 사항

            운영자는 사용자의 운동 기록을 분석하여 AI 기반 코칭 피드백을 제공합니다. 현재 본 서비스의 AI 피드백은 운동 개선을 위한 참고 정보 제공이며, 사용자의 권리 또는 의무에 중대한 영향을 미치는 합격·불합격, 승인·거절, 자격 제한 등의 결정을 자동으로 내리지 않습니다.

            ## 제12조 개인정보 전송요구권

            정보주체는 법령에서 정한 요건에 해당하는 경우 본인의 개인정보를 다른 개인정보처리자 또는 관리기관에 전송하도록 요구할 수 있습니다. 현재 서비스에서는 전송 요구 전용 기능을 제공하지 않으나, 이메일 또는 전화로 요청하면 본인 확인 후 가능한 범위에서 JSON 또는 CSV 형식으로 처리합니다.

            ## 제13조 마케팅 정보 수신 동의

            <span id="marketing-consent" />

            운영자는 별도 동의한 회원에게 서비스 업데이트, 이벤트, 혜택 안내 등 마케팅 정보를 이메일 등으로 발송할 수 있습니다. 회원은 언제든지 수신 동의를 철회할 수 있으며, 동의하지 않아도 기본 서비스 이용에는 제한이 없습니다.

            ## 제14조 정보주체의 권익침해 구제 방법

            - 개인정보분쟁조정위원회: 국번없이 1833-6972 (www.kopico.go.kr)
            - 개인정보침해신고센터: 국번없이 118 (privacy.kisa.or.kr)
            - 대검찰청: 국번없이 1301 (www.spo.go.kr)
            - 경찰청: 국번없이 182 (ecrm.police.go.kr)

            ## 제15조 개인정보 처리방침의 변경

            본 개인정보 처리방침은 시행일로부터 적용됩니다. 법령 또는 서비스 변경에 따라 방침이 변경되는 경우 변경사항의 시행 7일 전부터 서비스 화면 또는 공지사항을 통해 고지합니다. 정보주체에게 불리한 중요한 변경은 30일 이상의 사전 유예기간을 두고 안내합니다.

            ### 개정 이력

            - 2026.08.15: 개인정보 처리방침 최초 제정
            """;

    private static final String SENSITIVE_INFORMATION_CONTENT = """
            # 건강 관련 운동 데이터 처리 동의

            Workout AI Coach는 운동 분석과 AI 피드백 제공을 위해 심박수, 운동 강도, 속도, 파워, 케이던스 등 건강 관련 운동 데이터를 처리할 수 있습니다.

            이 데이터는 운동 기록 저장, 운동 상태 분석, AI 피드백 생성, 대시보드 통계 제공 목적으로만 사용됩니다. 동의하지 않는 경우 건강 관련 센서 데이터 기반 피드백 기능 이용이 제한될 수 있습니다.
            """;

    private static final String MARKETING_CONTENT = """
            # 마케팅 정보 수신 동의

            Workout AI Coach는 별도 동의한 회원에게 서비스 업데이트, 이벤트, 혜택 안내 등 마케팅 정보를 이메일 등으로 발송할 수 있습니다.

            회원은 언제든지 수신 동의를 철회할 수 있으며, 동의하지 않아도 기본 서비스 이용에는 제한이 없습니다.
            """;
}
