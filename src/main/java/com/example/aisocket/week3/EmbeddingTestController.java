package com.example.aisocket.week3;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class EmbeddingTestController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingTestController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/api/v1/test/embedding")
    public Map<String, Object> testEmbedding(@RequestParam String text) {

        float[] embedding = embeddingModel.embed(text);

        List<Float> sampleElements = new ArrayList<>();
        for (int i = 0; i < Math.min(5, embedding.length); i++) {
            sampleElements.add(embedding[i]);
        }

        return Map.of(
                "input_text", text,
                "vector_dimension", embedding.length,
                "sample_elements", sampleElements
        );
    }
}
