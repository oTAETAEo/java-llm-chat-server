package com.example.aisocket.week3.arg;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgvector.PGvector;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryCacheRegistry {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<InMemoryVectorRow> cacheRegistry = new ArrayList<>();

    public InMemoryCacheRegistry(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initInMemoryVectorCache() {

        String sql = "SELECT id, content, metadata, embedding FROM public.vector_store";

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            List<InMemoryVectorRow> tempCache = new ArrayList<>();

            for (Map<String, Object> row : rows) {

                String id = row.get("id").toString();
                String content = (String) row.get("content");
                String category = "";
                float[] floatEmbedding;

                Object metadataObj = row.get("metadata");
                if (metadataObj != null) {
                    Map<String, Object> metaMap = objectMapper.readValue(
                            metadataObj.toString(), new TypeReference<>() {});

                    category = (String) metaMap.getOrDefault("category", "");
                }

                Object embeddingObj = row.get("embedding");
                floatEmbedding = parseEmbedding(embeddingObj);

                if (floatEmbedding != null) {
                    tempCache.add(new InMemoryVectorRow(id, content, category, floatEmbedding));
                }
            }

            this.cacheRegistry = Collections.unmodifiableList(tempCache);

            log.info("{}개의 자바 지식 벡터가 힙 메모리에 올라감.", cacheRegistry.size());

        } catch (Exception e) {
            log.error("인메모리 벡터 로딩 중 오류 발생", e);
        }
    }

    public List<InMemoryVectorRow> getAllCachedVectors() {
        return this.cacheRegistry;
    }

    private float[] parseEmbedding(Object embeddingObj) {
        if (embeddingObj == null) {
            return null;
        }

        if (embeddingObj instanceof PGvector vector) {
            return vector.toArray();
        }

        String vecStr = embeddingObj.toString()
                .replace("[", "")
                .replace("]", "");

        String[] parts = vecStr.split(",");
        float[] embedding = new float[parts.length];

        for (int i = 0; i < parts.length; i++) {
            embedding[i] = Float.parseFloat(parts[i].trim());
        }

        return embedding;
    }
}