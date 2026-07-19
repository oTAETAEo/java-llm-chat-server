package com.example.aisocket.project.application.prompt;

import com.example.aisocket.project.application.prompt.template.PromptTemplate;
import com.example.aisocket.project.application.prompt.template.section.SingleWorkoutCommonSection;
import com.example.aisocket.project.application.prompt.template.section.SingleWorkoutHallucinationGuardSection;
import com.example.aisocket.project.application.prompt.template.section.SingleWorkoutOutputFormatSection;
import com.example.aisocket.project.application.prompt.template.SingleWorkoutPromptContext;
import com.example.aisocket.project.application.prompt.template.SingleWorkoutPromptTemplate;
import com.example.aisocket.project.application.prompt.template.section.SingleWorkoutSpecificSection;
import com.example.aisocket.project.application.prompt.template.section.SingleWorkoutSystemInstructionSection;
import com.example.aisocket.project.application.prompt.workout.WorkoutPromptSectionBuilder;
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
