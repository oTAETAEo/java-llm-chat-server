package com.example.aisocket.project.application.embedding;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

@Component
public class CyclingWorkoutEmbeddingContentBuilder implements WorkoutEmbeddingContentBuilder {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.CYCLING;
    }

    @Override
    public String build(Workout workout, AthleteTier tier) {
        if (!(workout instanceof CyclingWorkout cyclingWorkout)) {
            throw new IllegalArgumentException("자전거 운동 데이터가 아닙니다.");
        }

        return """
                [자전거 운동 기록]
                사용자 등급: %s
                운동 타입: %s
                거리: %.2f km
                운동 시간: %d 분
                소모 칼로리: %.1f kcal
                누적 상승 고도: %.1f m
                최고 고도: %.1f m
                평균 심박수: %.1f bpm
                최대 심박수: %.1f bpm
                평균 케이던스: %.1f rpm
                최대 케이던스: %.1f rpm
                평균 속도: %.1f km/h
                최고 속도: %.1f km/h
                평균 파워: %.1f W
                최고 파워: %.1f W
                FTP: %.1f W
                이 텍스트는 사용자의 자전거 운동 기록을 VectorDB에 임베딩하기 위한 요약입니다.
                """.formatted(
                tier,
                cyclingWorkout.getWorkOutType(),
                cyclingWorkout.getDistance(),
                cyclingWorkout.getMovingTime(),
                cyclingWorkout.getCalories(),
                cyclingWorkout.getElevGain(),
                cyclingWorkout.getElevationMax(),
                cyclingWorkout.getAvgHeartRate(),
                cyclingWorkout.getMaxHeartRate(),
                cyclingWorkout.getAvgCadence(),
                cyclingWorkout.getMaxCadence(),
                cyclingWorkout.getAvgSpeed(),
                cyclingWorkout.getMaxSpeed(),
                cyclingWorkout.getAvgPower(),
                cyclingWorkout.getMaxPower(),
                cyclingWorkout.getFtp()
        );
    }
}
