package com.example.aisocket.project.application.prompt.template.section;

import com.example.aisocket.project.domain.Workout;
import com.example.aisocket.project.application.prompt.template.SingleWorkoutPromptContext;
import org.springframework.stereotype.Component;

@Component
public class SingleWorkoutCommonSection implements SingleWorkoutPromptSection {

    @Override
    public int order() {
        return 2;
    }

    @Override
    public String render(SingleWorkoutPromptContext context) {
        Workout workout = context.workout();

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
