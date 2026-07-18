package com.example.aisocket.project.application.prompt;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SingleWorkoutPromptTemplate implements PromptTemplate<SingleWorkoutPromptContext> {

    private final List<SingleWorkoutPromptSection> sections;

    public SingleWorkoutPromptTemplate(List<SingleWorkoutPromptSection> sections) {
        this.sections = sections.stream()
                .sorted(Comparator.comparingInt(SingleWorkoutPromptSection::order))
                .toList();
    }

    @Override
    public String render(SingleWorkoutPromptContext context) {
        Objects.requireNonNull(context, "프롬프트 컨텍스트(context)는 필수 값입니다.");
        Objects.requireNonNull(context.workout(), "운동 데이터(workout)는 필수 값입니다.");

        return sections.stream()
                .map(section -> section.render(context))
                .collect(Collectors.joining("\n"));
    }
}
