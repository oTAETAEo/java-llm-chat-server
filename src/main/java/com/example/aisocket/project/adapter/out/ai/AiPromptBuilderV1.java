package com.example.aisocket.project.adapter.out.ai;

import com.example.aisocket.project.domain.*;
import lombok.NonNull;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class AiPromptBuilderV1 implements AiPromptBuilder {

    @Override
    public Prompt build(Workout workout, AthleteTier tier) {

        String systemInstruction = "";
        if (AthleteTier.PRO.equals(tier)) {

            systemInstruction = createProSystemInstruction();
        } else if (AthleteTier.AMATEUR.equals(tier)) {

            systemInstruction = createAmateurSystemInstruction();
        }

        String commonTemplate = createCommonPrompt(workout);

        String specificTemplate = "";
        if (workout.getWorkOutType() == WorkOutType.CYCLING){

            specificTemplate = createCycleWorkoutPrompt((CyclingWorkout) workout);
        } else if (workout.getWorkOutType() == WorkOutType.RUNNING){

            specificTemplate = createRunningWorkoutPrompt((RunningWorkout) workout);
        }

        return new Prompt(systemInstruction + commonTemplate + "\n" + specificTemplate);
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
                - 운동 거리: %.2f km
                - 운동 시간: %d 분
                - 소모 칼로리: %.1f kcal
                - 평균 심박수: %.1f bpm / 최대 심박수: %.1f bpm
                - 평균 케이던스: %.1f rpm / 최대 케이던스: %.1f rpm
                """.formatted(
                workout.getDistance(),
                workout.getMovingTime(),
                workout.getCalories(),
                workout.getAvgHeartRate(),
                workout.getMaxHeartRate(),
                workout.getAvgCadence(),
                workout.getMaxCadence()
        );
    }

    private @NonNull String createCycleWorkoutPrompt(CyclingWorkout cyclingWorkout) {
        return """       
                [자전거 전용 분석 지표]
                - 평균 속도: %.1f km/h / 최고 속도: %.1f km/h
                - 평균 파워: %.1f W / 최고 파워: %.1f W
                - 현재 유저 FTP: %.1f W
                
                코칭 매뉴얼: 자전거 전용 지표인 FTP 대비 평균 파워(avgPower)의 비율을 분석하여 오버페이스 여부를 판별하고, 라이딩 효율성 향상을 위한 페이싱 전략을 3문장 이내로 제시하세요.
                
                """.formatted(
                cyclingWorkout.getAvgSpeed(),
                cyclingWorkout.getMaxSpeed(),
                cyclingWorkout.getAvgPower(),
                cyclingWorkout.getMaxPower(),
                cyclingWorkout.getFtp()
        );
    }

    private @NonNull String createRunningWorkoutPrompt(RunningWorkout runningWorkout) {
        return """
                [러닝 전용 분석 지표]
                - 평균 페이스: %.2f min/km / 최고 페이스: %.2f min/km
                - 총 걸음 수: %d 걸음
                
                코칭 매뉴얼: 러닝 전용 지표인 평균 페이스(avgPace)와 총 걸음 수를 분석하여 주폭(Stride)과 케이던스의 밸런스가 적절했는지 판별하고, 부상 방지 및 페이스 유지 조언을 3문장 이내로 제시하세요.
                
                """.formatted(
                runningWorkout.getAvgPace(),
                runningWorkout.getMaxPace(),
                runningWorkout.getSteps()
        );
    }
}