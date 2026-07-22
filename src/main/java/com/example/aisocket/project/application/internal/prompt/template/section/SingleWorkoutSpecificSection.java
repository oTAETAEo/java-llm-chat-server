package com.example.aisocket.project.application.internal.prompt.template.section;

import com.example.aisocket.project.application.internal.prompt.workout.WorkoutPromptSectionBuilder;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import com.example.aisocket.project.application.internal.prompt.template.SingleWorkoutPromptContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SingleWorkoutSpecificSection implements SingleWorkoutPromptSection {

    private final Map<WorkOutType, WorkoutPromptSectionBuilder> sectionBuilders;

    public SingleWorkoutSpecificSection(List<WorkoutPromptSectionBuilder> sectionBuilders) {
        this.sectionBuilders = sectionBuilders.stream()
                .collect(Collectors.toMap(
                        WorkoutPromptSectionBuilder::supportType,
                        Function.identity()
                ));
    }

    @Override
    public int order() {
        return 3;
    }

    @Override
    public String render(SingleWorkoutPromptContext context) {
        Workout workout = context.workout();
        WorkoutPromptSectionBuilder sectionBuilder = sectionBuilders.get(workout.getWorkOutType());
        if (sectionBuilder == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + workout.getWorkOutType());
        }

        return sectionBuilder.build(workout);
    }
}
