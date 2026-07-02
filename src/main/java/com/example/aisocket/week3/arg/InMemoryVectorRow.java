package com.example.aisocket.week3.arg;

public record InMemoryVectorRow(
        String id,
        String content,
        String category,
        float[] embedding
) {
}