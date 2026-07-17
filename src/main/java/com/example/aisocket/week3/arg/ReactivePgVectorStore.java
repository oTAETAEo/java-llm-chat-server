package com.example.aisocket.week3.arg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ReactivePgVectorStore {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper;
    private final String tableName;

    public ReactivePgVectorStore(
            JdbcTemplate jdbcTemplate,
            EmbeddingModel embeddingModel,
            ObjectMapper objectMapper,
            @Value("${app.vector-store.table-name:vector_store}") String tableName
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingModel = embeddingModel;
        this.objectMapper = objectMapper;
        this.tableName = tableName;
    }

    public void add(String content, String category) {
        insert(content, category, embed(content));
    }

    public List<String> similaritySearch(String query, String category, int topK, double similarityThreshold) {
        String embedding = toVectorLiteral(embed(query));
        return jdbcTemplate.queryForList("""
                        SELECT content
                        FROM public.%s
                        WHERE metadata ->> 'category' = ?
                          AND 1 - (embedding <=> CAST(? AS vector)) >= ?
                        ORDER BY embedding <=> CAST(? AS vector)
                        LIMIT ?
                        """.formatted(tableName),
                String.class,
                category,
                embedding,
                similarityThreshold,
                embedding,
                topK
        );
    }

    public List<InMemoryVectorRow> findAll() {
        return jdbcTemplate.query("""
                        SELECT id, content, metadata::text AS metadata, embedding::text AS embedding
                        FROM public.%s
                        """.formatted(tableName),
                (row, rowNum) -> new InMemoryVectorRow(
                        String.valueOf(row.getObject("id")),
                        row.getString("content"),
                        extractCategory(row.getString("metadata")),
                        parseEmbedding(row.getString("embedding"))
                ));
    }

    private void insert(String content, String category, float[] embedding) {
        jdbcTemplate.update("""
                        INSERT INTO public.%s (id, content, metadata, embedding)
                        VALUES (CAST(? AS uuid), ?, CAST(? AS jsonb), CAST(? AS vector))
                        """.formatted(tableName),
                UUID.randomUUID().toString(),
                content,
                toMetadataJson(category),
                toVectorLiteral(embedding)
        );
    }

    private float[] embed(String text) {
        return embeddingModel.embed(text);
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
