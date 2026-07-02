package com.example.aisocket.week3.arg;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryUnrolledRagService {

    private final EmbeddingModel embeddingModel;
    private final InMemoryCacheRegistry cacheRegistry;

    public List<String> searchTopK(QueryPayload payload) {

        float[] queryEmbedding = embeddingModel.embed(payload.question());

        List<InMemoryVectorRow> allVectors = cacheRegistry.getAllCachedVectors();
        List<ScoredRow> scoredRows = new ArrayList<>();

        String targetCategory = payload.category();

        for (InMemoryVectorRow row : allVectors) {
            if (!row.category().equalsIgnoreCase(targetCategory)) {
                continue;
            }

            double similarity = calculateUnrolledCosineSimilarity(queryEmbedding, row.embedding());
            scoredRows.add(new ScoredRow(row, similarity));
        }

        scoredRows.sort(Comparator.comparingDouble(ScoredRow::score).reversed());

        return getTop5ScoredRows(scoredRows);
    }

    private @NonNull List<String> getTop5ScoredRows(List<ScoredRow> scoredRows) {
        List<String> result = new ArrayList<>();
        int limit = Math.min(5, scoredRows.size());

        for (int i = 0; i < limit; i++) {
            result.add(scoredRows.get(i).row().content());
        }

        return result;
    }

    private double calculateUnrolledCosineSimilarity(float[] vecA, float[] vecB) {
        int len = vecA.length;
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        int i = 0;
        for (; i <= len - 4; i += 4) {
            dotProduct += vecA[i] * vecB[i]; normA += vecA[i] * vecA[i]; normB += vecB[i] * vecB[i];
            dotProduct += vecA[i+1] * vecB[i+1]; normA += vecA[i+1] * vecA[i+1]; normB += vecB[i+1] * vecB[i+1];
            dotProduct += vecA[i+2] * vecB[i+2]; normA += vecA[i+2] * vecA[i+2]; normB += vecB[i+2] * vecB[i+2];
            dotProduct += vecA[i+3] * vecB[i+3]; normA += vecA[i+3] * vecA[i+3]; normB += vecB[i+3] * vecB[i+3];
        }

        for (; i < len; i++) {
            dotProduct += vecA[i] * vecB[i]; normA += vecA[i] * vecA[i]; normB += vecB[i] * vecB[i];
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private record ScoredRow(InMemoryVectorRow row, double score) {}
}