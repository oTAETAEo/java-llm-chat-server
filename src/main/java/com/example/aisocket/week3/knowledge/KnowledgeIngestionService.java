package com.example.aisocket.week3.knowledge;

import com.example.aisocket.week3.arg.InMemoryCacheRegistry;
import com.example.aisocket.week3.arg.ReactivePgVectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KnowledgeIngestionService {

    private final ReactivePgVectorStore vectorStore;
    private final InMemoryCacheRegistry cacheRegistry;

    public KnowledgeIngestionService(ReactivePgVectorStore vectorStore, InMemoryCacheRegistry cacheRegistry) {
        this.vectorStore = vectorStore;
        this.cacheRegistry = cacheRegistry;
    }

    public Mono<Void> injectKnowledge(KnowledgePayload payload) {
        return vectorStore.add(payload.context(), payload.category())
                .then(cacheRegistry.refresh());
    }

}
