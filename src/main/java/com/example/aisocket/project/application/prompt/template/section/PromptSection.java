package com.example.aisocket.project.application.prompt.template.section;

public interface PromptSection<C> {

    int order();

    String render(C context);
}
