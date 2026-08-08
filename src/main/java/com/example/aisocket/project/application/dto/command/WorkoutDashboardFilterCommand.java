package com.example.aisocket.project.application.dto.command;

import com.example.aisocket.project.domain.WorkOutType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkoutDashboardFilterCommand(
        String period,
        String workOutType,
        LocalDate startDate,
        LocalDate endDate
) {

    public WorkOutType filteredWorkoutType() {
        if (workOutType == null || workOutType.isBlank() || "ALL".equalsIgnoreCase(workOutType)) {
            return null;
        }
        return WorkOutType.valueOf(workOutType.toUpperCase());
    }

    public LocalDateTime startedAtFrom() {
        if ("custom".equalsIgnoreCase(period)) {
            return startDate == null ? null : startDate.atStartOfDay();
        }

        LocalDate today = LocalDate.now();
        return switch (period == null ? "ALL" : period) {
            case "7d" -> today.minusDays(7).atStartOfDay();
            case "30d" -> today.minusDays(30).atStartOfDay();
            case "90d" -> today.minusDays(90).atStartOfDay();
            default -> null;
        };
    }

    public LocalDateTime startedAtToExclusive() {
        if (!"custom".equalsIgnoreCase(period) || endDate == null) {
            return null;
        }
        return endDate.plusDays(1).atStartOfDay();
    }
}
