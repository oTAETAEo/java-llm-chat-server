package com.example.aisocket.project.application.prompt.workout;

import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CyclingWorkoutPromptSectionBuilder implements WorkoutPromptSectionBuilder {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.CYCLING;
    }

    @Override
    public String build(Workout workout) {
        if (!(workout instanceof CyclingWorkout cyclingWorkout)) {
            throw new IllegalArgumentException("자전거 운동 데이터가 아닙니다.");
        }

        return createCycleWorkoutPrompt(cyclingWorkout);
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
}
