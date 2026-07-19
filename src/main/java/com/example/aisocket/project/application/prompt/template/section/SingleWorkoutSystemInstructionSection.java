package com.example.aisocket.project.application.prompt.template.section;

import com.example.aisocket.project.application.prompt.template.SingleWorkoutPromptContext;
import org.springframework.stereotype.Component;

@Component
public class SingleWorkoutSystemInstructionSection implements SingleWorkoutPromptSection {

    @Override
    public int order() {
        return 1;
    }

    @Override
    public String render(SingleWorkoutPromptContext context) {
        if (context.tier() == null) {
            throw new IllegalArgumentException("운동 등급(tier)은 필수 값입니다.");
        }

        return switch (context.tier()) {
            case PRO -> createProSystemInstruction();
            case AMATEUR -> createAmateurSystemInstruction();
        };
    }

    private String createProSystemInstruction() {
        return """
                당신은 개인 맞춤형 피드백을 제공하는 전문 AI 운동 코치입니다.
                현재 피드백을 받는 사용자의 등급 수준은 [PRO] 입니다.
                
                중요 코칭 규칙:
                매우 정밀하고 생체역학적인 전문 용어를 사용하여 정량적이고 엄격한 훈련 피드백을 제시하세요.
                목표 달성과 데이터 기반의 퍼포먼스 향상에 초점을 맞추어 단호하고 전문적인 톤으로 작성하세요.
                
                제공된 운동 데이터를 기반으로 상황에 맞는 정밀 피드백을 작성하세요.
                
                """;
    }

    private String createAmateurSystemInstruction() {
        return """
                당신은 개인 맞춤형 피드백을 제공하는 전문 AI 운동 코치입니다.
                현재 피드백을 받는 사용자의 등급 수준은 [AMATEUR] 입니다.
                
                중요 코칭 규칙:
                부상 방지와 지속 가능한 운동 루틴, 그리고 운동의 즐거움에 초점을 맞추어 조언하세요.
                어려운 생체역학 용어는 배제하고, 이해하기 쉽고 따뜻하며 친절한 톤으로 피드백을 제시하세요.
                
                제공된 운동 데이터를 기반으로 상황에 맞는 정밀 피드백을 작성하세요.
                
                """;
    }
}
