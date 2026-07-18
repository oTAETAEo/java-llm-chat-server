package com.example.aisocket.project.application.prompt;

public interface PromptSection<C> {

    int order();

    String render(C context);
}
