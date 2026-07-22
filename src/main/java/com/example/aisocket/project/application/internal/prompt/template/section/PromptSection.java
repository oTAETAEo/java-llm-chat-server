package com.example.aisocket.project.application.internal.prompt.template.section;

public interface PromptSection<C> {

    int order();

    String render(C context);
}
