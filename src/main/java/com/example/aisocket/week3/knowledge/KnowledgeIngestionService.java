package com.example.aisocket.week3.knowledge;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KnowledgeIngestionService {

    private final VectorStore vectorStore;

    public KnowledgeIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void injectKnowledge(KnowledgePayload payload) {

        Document document = Document.builder()
                .text(payload.context())
                .metadata(Map.of("category", payload.category()))
                .build();

        vectorStore.accept(List.of(document));
    }

}
