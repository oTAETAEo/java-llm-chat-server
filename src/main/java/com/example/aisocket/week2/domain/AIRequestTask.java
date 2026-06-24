package com.example.aisocket.week2.domain;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
public class AIRequestTask implements Comparable<AIRequestTask> {

    private final String prompt;
    private final UserGrade userGrade;
    private final long requestedTime;
    private final CompletableFuture<String> responseFuture;
    private int retryCount;

    public AIRequestTask(String prompt, UserGrade userGrade, CompletableFuture<String> responseFuture) {
        this.prompt = prompt;
        this.userGrade = userGrade;
        this.requestedTime = System.currentTimeMillis();
        this.responseFuture = responseFuture;
        this.retryCount = 0;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    @Override
    public int compareTo(AIRequestTask other) {
        int gradeCompare = Integer.compare(other.userGrade.getPriority(), this.userGrade.getPriority());

        if (gradeCompare != 0) {
            return gradeCompare;
        }

        return Long.compare(this.requestedTime, other.requestedTime);
    }

}