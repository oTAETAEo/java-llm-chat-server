package com.example.aisocket.week3.arg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ReactivePgVectorStore {

    private final DatabaseClient databaseClient;
    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper;
    private final String tableName;

    public ReactivePgVectorStore(
            DatabaseClient databaseClient,
            EmbeddingModel embeddingModel,
            ObjectMapper objectMapper,
            @Value("${app.vector-store.table-name:vector_store}") String tableName
    ) {
        this.databaseClient = databaseClient;
        this.embeddingModel = embeddingModel;
        this.objectMapper = objectMapper;
        this.tableName = tableName;
    }

    public Mono<Void> add(String content, String category) {
        return embed(content)
                .flatMap(embedding -> insert(content, category, embedding));
    }

    public Flux<String> similaritySearch(String query, String category, int topK, double similarityThreshold) {
        return embed(query)
                .flatMapMany(embedding -> databaseClient.sql("""
                                SELECT content,
                                       1 - (embedding <=> CAST(:embedding AS vector)) AS similarity
                                FROM public.%s
                                WHERE metadata ->> 'category' = :category
                                  AND 1 - (embedding <=> CAST(:embedding AS vector)) >= :similarityThreshold
                                ORDER BY embedding <=> CAST(:embedding AS vector)
                                LIMIT :topK
                                """.formatted(tableName))
                        .bind("embedding", toVectorLiteral(embedding))
                        .bind("category", category)
                        .bind("similarityThreshold", similarityThreshold)
                        .bind("topK", topK)
                        .map((row, metadata) -> row.get("content", String.class))
                        .all());
    }

    public Flux<InMemoryVectorRow> findAll() {
        return databaseClient.sql("""
                        SELECT id, content, metadata::text AS metadata, embedding::text AS embedding
                        FROM public.%s
                        """.formatted(tableName))
                .map((row, metadata) -> {
                    String id = String.valueOf(row.get("id"));
                    String content = row.get("content", String.class);
                    String metadataJson = row.get("metadata", String.class);
                    String embeddingText = row.get("embedding", String.class);

                    return new InMemoryVectorRow(
                            id,
                            content,
                            extractCategory(metadataJson),
                            parseEmbedding(embeddingText)
                    );
                })
                .all();
    }

    private Mono<Void> insert(String content, String category, float[] embedding) {
        return databaseClient.sql("""
                        INSERT INTO public.%s (id, content, metadata, embedding)
                        VALUES (CAST(:id AS uuid), :content, CAST(:metadata AS jsonb), CAST(:embedding AS vector))
                        """.formatted(tableName))
                .bind("id", UUID.randomUUID().toString())
                .bind("content", content)
                .bind("metadata", toMetadataJson(category))
                .bind("embedding", toVectorLiteral(embedding))
                .fetch()
                .rowsUpdated()
                .then();
    }

    private Mono<float[]> embed(String text) {
        return Mono.fromCallable(() -> embeddingModel.embed(text))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String toMetadataJson(String category) {
        try {
            return objectMapper.writeValueAsString(Map.of("category", category));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("metadata JSON 변환에 실패했습니다.", e);
        }
    }

    private String extractCategory(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            return "";
        }

        try {
            Map<?, ?> metadata = objectMapper.readValue(metadataJson, Map.class);
            Object category = metadata.get("category");
            return category == null ? "" : category.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String toVectorLiteral(float[] embedding) {
        return IntStream.range(0, embedding.length)
                .mapToObj(i -> Float.toString(embedding[i]))
                .collect(Collectors.joining(",", "[", "]"));
    }

    private float[] parseEmbedding(String embeddingText) {
        if (embeddingText == null || embeddingText.isBlank()) {
            return new float[0];
        }

        String normalized = embeddingText.replace("[", "").replace("]", "");
        if (normalized.isBlank()) {
            return new float[0];
        }

        String[] parts = normalized.split(",");
        float[] embedding = new float[parts.length];

        for (int i = 0; i < parts.length; i++) {
            embedding[i] = Float.parseFloat(parts[i].trim());
        }

        return embedding;
    }
}
