package com.example.aisocket.project.application.prompt.template;

public interface PromptTemplate<C> {

    String render(C context);
}
