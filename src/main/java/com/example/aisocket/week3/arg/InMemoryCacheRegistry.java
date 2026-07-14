package com.example.aisocket.week3.arg;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class InMemoryCacheRegistry {

    private final ReactivePgVectorStore vectorStore;

    private volatile List<InMemoryVectorRow> cacheRegistry = new ArrayList<>();

    public InMemoryCacheRegistry(ReactivePgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void initInMemoryVectorCache() {
        refresh()
                .doOnSuccess(ignored -> log.info("{}개의 자바 지식 벡터가 힙 메모리에 올라감.", cacheRegistry.size()))
                .doOnError(error -> log.error("인메모리 벡터 로딩 중 오류 발생", error))
                .subscribe();
    }

    public Mono<Void> refresh() {
        return vectorStore.findAll()
                .collectList()
                .doOnNext(rows -> this.cacheRegistry = Collections.unmodifiableList(rows))
                .then();
    }

    public List<InMemoryVectorRow> getAllCachedVectors() {
        return this.cacheRegistry;
    }
}
