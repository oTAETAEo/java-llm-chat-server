package com.example.aisocket.week3.knowledge;

import com.example.aisocket.week3.arg.InMemoryCacheRegistry;
import com.example.aisocket.week3.arg.ReactivePgVectorStore;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeIngestionService {

    private final ReactivePgVectorStore vectorStore;
    private final InMemoryCacheRegistry cacheRegistry;

    public KnowledgeIngestionService(ReactivePgVectorStore vectorStore, InMemoryCacheRegistry cacheRegistry) {
        this.vectorStore = vectorStore;
        this.cacheRegistry = cacheRegistry;
    }

    public void injectKnowledge(KnowledgePayload payload) {
        vectorStore.add(payload.context(), payload.category());
        cacheRegistry.refresh();
    }

}
