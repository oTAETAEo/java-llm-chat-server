package com.example.aisocket.project.domain;

public final class WorkoutTitle {

    private WorkoutTitle() {
    }

    public static String defaultTitle(WorkOutType workOutType, Double distance) {
        String workoutName = workoutName(workOutType);
        if (distance == null) {
            return workoutName + " 운동";
        }
        return "%s %.1fkm".formatted(workoutName, distance);
    }

    public static String normalizeOrDefault(String title, WorkOutType workOutType, Double distance) {
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        return defaultTitle(workOutType, distance);
    }

    private static String workoutName(WorkOutType workOutType) {
        if (workOutType == WorkOutType.RUNNING) {
            return "러닝";
        }
        if (workOutType == WorkOutType.CYCLING) {
            return "자전거";
        }
        throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + workOutType);
    }
}
