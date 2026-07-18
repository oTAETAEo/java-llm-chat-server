package com.example.aisocket.project.application.prompt;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoachFeedbackPromptBuilder implements AiPromptBuilder {

    private final PromptTemplate<SingleWorkoutPromptContext> singleWorkoutPromptTemplate;

    @Autowired
    public CoachFeedbackPromptBuilder(SingleWorkoutPromptTemplate singleWorkoutPromptTemplate) {
        this.singleWorkoutPromptTemplate = singleWorkoutPromptTemplate;
    }

    public CoachFeedbackPromptBuilder(List<WorkoutPromptSectionBuilder> sectionBuilders) {
        this(new SingleWorkoutPromptTemplate(List.of(
                new SingleWorkoutSystemInstructionSection(),
                new SingleWorkoutCommonSection(),
                new SingleWorkoutSpecificSection(sectionBuilders),
                new SingleWorkoutHallucinationGuardSection(),
                new SingleWorkoutOutputFormatSection()
        )));
    }

    @Override
    public String build(Workout workout, AthleteTier tier) {
        return singleWorkoutPromptTemplate.render(new SingleWorkoutPromptContext(workout, tier));
    }
}
