package com.example.aisocket.project.application.internal.prompt.template;

public interface PromptTemplate<C> {

    String render(C context);
}
