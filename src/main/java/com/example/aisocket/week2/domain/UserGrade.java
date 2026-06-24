package com.example.aisocket.week2.domain;

public enum UserGrade {
    HIGH(2),
    LOW(1);

    private final int priority;

    UserGrade(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}