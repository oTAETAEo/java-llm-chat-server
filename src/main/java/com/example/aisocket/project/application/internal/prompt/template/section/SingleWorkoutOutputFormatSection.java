package com.example.aisocket.project.application.internal.prompt.template.section;

import com.example.aisocket.project.application.internal.prompt.template.SingleWorkoutPromptContext;
import org.springframework.stereotype.Component;

@Component
public class SingleWorkoutOutputFormatSection implements SingleWorkoutPromptSection {

    @Override
    public int order() {
        return 5;
    }

    @Override
    public String render(SingleWorkoutPromptContext context) {
        return """
                [응답 형식]
                - 반드시 한국어 HTML 조각(fragment)만 응답하세요. Markdown을 사용하지 마세요.
                - 허용 태그만 사용하세요: article, section, h1, h2, p, ul, li, strong, br.
                - script, style, iframe, img, a 태그와 모든 HTML 속성은 절대 사용하지 마세요.
                - 아래 제목과 순서를 정확히 지키세요. 제목 문구를 바꾸거나 번호를 붙이지 마세요.
                - `🎯 Next Mission`은 반드시 `<ul>` 안에 `<li>` 3개로 작성하세요.
                - 모든 한국어 문장은 자연스러운 띄어쓰기를 지키세요.

                <article>
                  <h1>⚡ AI 운동 리포트</h1>

                  <section>
                    <h2>🏅 오늘의 한줄 평가</h2>
                    <p>20~30자 한 문장으로 작성하세요.</p>
                  </section>

                  <section>
                    <h2>📈 성장 포인트</h2>
                    <p>운동 스타일, 잘한 점, 개선점을 자연스러운 띄어쓰기로 2~3문장 작성하세요.</p>
                  </section>

                  <section>
                    <h2>📊 트레이닝 인사이트</h2>
                    <p>이번 운동에서 발견한 특징 1가지를 자연스러운 띄어쓰기로 1~2문장 작성하세요.</p>
                  </section>

                  <section>
                    <h2>💬 운동 코치 총평</h2>
                    <p>동기부여와 칭찬을 담아 자연스러운 띄어쓰기로 2~3문장 작성하세요.</p>
                  </section>

                  <section>
                    <h2>🎯 Next Mission</h2>
                    <ul>
                      <li>구체적인 목표 1</li>
                      <li>구체적인 목표 2</li>
                      <li>구체적인 목표 3</li>
                    </ul>
                  </section>
                </article>
                """;
    }
}
