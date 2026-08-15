package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.command.WorkoutDashboardFilterCommand;
import com.example.aisocket.project.application.dto.result.CursorPageResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardSummaryResult;
import com.example.aisocket.project.application.dto.result.WorkoutHistoryItemResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface WorkoutDashboardService {

    WorkoutDashboardSummaryResult getSummary(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "운동 대시보드 필터(filter)는 필수 값입니다.") @Valid WorkoutDashboardFilterCommand filter
    );

    WorkoutDashboardInsightResult getInsights(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "운동 대시보드 필터(filter)는 필수 값입니다.") @Valid WorkoutDashboardFilterCommand filter
    );

    CursorPageResult<WorkoutHistoryItemResult> getHistories(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "운동 대시보드 필터(filter)는 필수 값입니다.") @Valid WorkoutDashboardFilterCommand filter,
            String cursor,
            Integer size
    );
}
