package com.example.aisocket.project.application.prompt.workout;

import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class RunningWorkoutPromptSectionBuilder implements WorkoutPromptSectionBuilder {

    @Override
    public WorkOutType supportType() {
        return WorkOutType.RUNNING;
    }

    @Override
    public String build(Workout workout) {
        if (!(workout instanceof RunningWorkout runningWorkout)) {
            throw new IllegalArgumentException("러닝 운동 데이터가 아닙니다.");
        }

        return createRunningWorkoutPrompt(runningWorkout);
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
