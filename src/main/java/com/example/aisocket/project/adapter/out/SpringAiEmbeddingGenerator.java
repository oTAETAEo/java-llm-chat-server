package com.example.aisocket.project.adapter.out;

import com.example.aisocket.project.application.out.EmbeddingGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringAiEmbeddingGenerator implements EmbeddingGenerator {

    private final EmbeddingModel embeddingModel;

    @Override
    public float[] generate(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("임베딩 생성 content는 필수 값입니다.");
        }

        return embeddingModel.embed(content);
    }
}
