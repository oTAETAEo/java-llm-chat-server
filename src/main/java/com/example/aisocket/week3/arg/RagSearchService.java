package com.example.aisocket.week3.arg;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagSearchService {

    private final VectorStore vectorStore;

    public RagSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<String> retrieveTop5FromDb(QueryPayload payload) {

        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression filterExpression = b.eq("category", payload.category()).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query(payload.question())
                .filterExpression(filterExpression)
                .topK(5)
                .similarityThreshold(0.5)
                .build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        return similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }
}