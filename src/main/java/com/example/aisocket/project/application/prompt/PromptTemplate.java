package com.example.aisocket.project.application.prompt;

public interface PromptTemplate<C> {

    String render(C context);
}
