package com.example.aisocket.project.application.internal.prompt.template.section;

import com.example.aisocket.project.application.internal.prompt.template.SingleWorkoutPromptContext;
import org.springframework.stereotype.Component;

@Component
public class SingleWorkoutHallucinationGuardSection implements SingleWorkoutPromptSection {

    @Override
    public int order() {
        return 4;
    }

    @Override
    public String render(SingleWorkoutPromptContext context) {
        return """
                [응답 제한 규칙]
                - 반드시 제공된 운동 데이터에 근거해서만 피드백하세요.
                - 제공되지 않은 신체 정보, 질병 정보, 과거 운동 이력은 추측하지 마세요.
                - 판단할 수 없는 내용은 "제공된 데이터만으로는 판단하기 어렵습니다"라고 답하세요.
                """;
    }
}
