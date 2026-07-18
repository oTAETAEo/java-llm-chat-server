package com.example.aisocket.project.application.prompt;

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
                1. 운동 요약
                2. 잘한 점
                3. 개선할 점
                4. 다음 운동 제안
                5. 주의사항
                """;
    }
}
