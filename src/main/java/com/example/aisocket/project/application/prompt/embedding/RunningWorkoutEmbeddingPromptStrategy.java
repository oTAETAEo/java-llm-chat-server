package com.example.aisocket.project.application.prompt.embedding;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

@Component
public class RunningWorkoutEmbeddingPromptStrategy implements WorkoutEmbeddingPromptStrategy {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.RUNNING;
    }

    @Override
    public String build(Workout workout, AthleteTier tier) {
        if (!(workout instanceof RunningWorkout runningWorkout)) {
            throw new IllegalArgumentException("러닝 운동 데이터가 아닙니다.");
        }

        return """
                [러닝 운동 기록]
                사용자 등급: %s
                운동 타입: %s
                운동 시작 시간: %s
                운동 종료 시간: %s
                거리: %.2f km
                운동 시간: %d 분
                소모 칼로리: %.1f kcal
                누적 상승 고도: %.1f m
                최고 고도: %.1f m
                평균 심박수: %.1f bpm
                최대 심박수: %.1f bpm
                평균 케이던스: %.1f rpm
                최대 케이던스: %.1f rpm
                평균 페이스: %.2f min/km
                최고 페이스: %.2f min/km
                걸음 수: %d
                이 텍스트는 사용자의 러닝 운동 기록을 VectorDB에 임베딩하기 위한 요약입니다.
                """.formatted(
                tier,
                runningWorkout.getWorkOutType(),
                runningWorkout.getStartedAt(),
                runningWorkout.getEndedAt(),
                runningWorkout.getDistance(),
                runningWorkout.getMovingTime(),
                runningWorkout.getCalories(),
                runningWorkout.getElevGain(),
                runningWorkout.getElevationMax(),
                runningWorkout.getAvgHeartRate(),
                runningWorkout.getMaxHeartRate(),
                runningWorkout.getAvgCadence(),
                runningWorkout.getMaxCadence(),
                runningWorkout.getAvgPace(),
                runningWorkout.getMaxPace(),
                runningWorkout.getSteps()
        );
    }
}
