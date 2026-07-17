package com.example.aisocket.project.application.prompt;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CoachFeedbackPromptBuilder implements AiPromptBuilder {

    private final Map<WorkOutType, WorkoutPromptSectionBuilder> sectionBuilders;

    public CoachFeedbackPromptBuilder(List<WorkoutPromptSectionBuilder> sectionBuilders) {
        this.sectionBuilders = sectionBuilders.stream()
                .collect(Collectors.toMap(
                        WorkoutPromptSectionBuilder::supportType,
                        Function.identity()
                ));
    }

    @Override
    public String build(Workout workout, AthleteTier tier) {
        Objects.requireNonNull(workout, "운동 데이터(workout)는 필수 값입니다.");

        String systemInstruction = createSystemInstruction(tier);
        String commonTemplate = createCommonPrompt(workout);
        String specificTemplate = createSpecificPrompt(workout);

        return systemInstruction + commonTemplate + "\n" + specificTemplate;
    }

    private String createSystemInstruction(AthleteTier tier) {
        if (tier == null) {
            throw new IllegalArgumentException("운동 등급(tier)은 필수 값입니다.");
        }

        return switch (tier) {
            case PRO -> createProSystemInstruction();
            case AMATEUR -> createAmateurSystemInstruction();
        };
    }

    private String createSpecificPrompt(Workout workout) {
        WorkoutPromptSectionBuilder sectionBuilder = sectionBuilders.get(workout.getWorkOutType());
        if (sectionBuilder == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + workout.getWorkOutType());
        }

        return sectionBuilder.build(workout);
    }

    private @NonNull String createProSystemInstruction() {
        return """
                당신은 개인 맞춤형 피드백을 제공하는 전문 AI 운동 코치입니다.
                현재 피드백을 받는 사용자의 등급 수준은 [PRO] 입니다.
                
                중요 코칭 규칙:
                매우 정밀하고 생체역학적인 전문 용어를 사용하여 정량적이고 엄격한 훈련 피드백을 제시하세요.
                목표 달성과 데이터 기반의 퍼포먼스 향상에 초점을 맞추어 단호하고 전문적인 톤으로 작성하세요.
                
                제공된 운동 데이터를 기반으로 상황에 맞는 정밀 피드백을 작성하세요.
                
                """;
    }

    private @NonNull String createAmateurSystemInstruction() {
        return """
                당신은 개인 맞춤형 피드백을 제공하는 전문 AI 운동 코치입니다.
                현재 피드백을 받는 사용자의 등급 수준은 [AMATEUR] 입니다.
                
                중요 코칭 규칙:
                부상 방지와 지속 가능한 운동 루틴, 그리고 운동의 즐거움에 초점을 맞추어 조언하세요.
                어려운 생체역학 용어는 배제하고, 이해하기 쉽고 따뜻하며 친절한 톤으로 피드백을 제시하세요.
                
                제공된 운동 데이터를 기반으로 상황에 맞는 정밀 피드백을 작성하세요.
                
                """;
    }

    private @NonNull String createCommonPrompt(Workout workout) {
        return """
                [기본 운동 통계 정보]
                - 운동 시작 시간: %s
                - 운동 종료 시간: %s
                - 운동 거리: %.2f km
                - 운동 시간: %d 분
                - 소모 칼로리: %.1f kcal
                - 평균 심박수: %.1f bpm / 최대 심박수: %.1f bpm
                - 평균 케이던스: %.1f rpm / 최대 케이던스: %.1f rpm
                """.formatted(
                workout.getStartedAt(),
                workout.getEndedAt(),
                workout.getDistance(),
                workout.getMovingTime(),
                workout.getCalories(),
                workout.getAvgHeartRate(),
                workout.getMaxHeartRate(),
                workout.getAvgCadence(),
                workout.getMaxCadence()
        );
    }
}
