package com.example.aisocket.project.adapter.out.ai;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Workout;
import org.springframework.ai.chat.prompt.Prompt;

public interface AiPromptBuilder {

    Prompt build(Workout workout, AthleteTier tier);

}
