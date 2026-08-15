package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.response.CursorPageResponse;
import com.example.aisocket.project.adapter.in.dto.response.WorkoutDashboardInsightResponse;
import com.example.aisocket.project.adapter.in.dto.response.WorkoutDashboardSummaryResponse;
import com.example.aisocket.project.adapter.in.dto.response.WorkoutHistoryItemResponse;
import com.example.aisocket.project.adapter.in.security.AuthenticationMember;
import com.example.aisocket.project.application.dto.command.WorkoutDashboardFilterCommand;
import com.example.aisocket.project.application.in.WorkoutDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/workouts/dashboard")
@RequiredArgsConstructor
public class WorkoutDashboardController {

    private final WorkoutDashboardService workoutDashboardService;

    @GetMapping("/summary")
    public WorkoutDashboardSummaryResponse getSummary(
            @AuthenticationMember Long memberId,
            @RequestParam(defaultValue = "ALL") String period,
            @RequestParam(defaultValue = "ALL") String workOutType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return WorkoutDashboardSummaryResponse.from(
                workoutDashboardService.getSummary(
                        memberId,
                        new WorkoutDashboardFilterCommand(period, workOutType, startDate, endDate)));
    }

    @GetMapping("/insights")
    public WorkoutDashboardInsightResponse getInsights(
            @AuthenticationMember Long memberId,
            @RequestParam(defaultValue = "ALL") String period,
            @RequestParam(defaultValue = "ALL") String workOutType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return WorkoutDashboardInsightResponse.from(
                workoutDashboardService.getInsights(
                        memberId,
                        new WorkoutDashboardFilterCommand(period, workOutType, startDate, endDate)));
    }

    @GetMapping("/histories")
    public CursorPageResponse<WorkoutHistoryItemResponse> getHistories(
            @AuthenticationMember Long memberId,
            @RequestParam(defaultValue = "ALL") String period,
            @RequestParam(defaultValue = "ALL") String workOutType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer size) {

        return CursorPageResponse.from(
                workoutDashboardService.getHistories(
                        memberId,
                        new WorkoutDashboardFilterCommand(period, workOutType, startDate, endDate),
                        cursor,
                        size
                ),
                WorkoutHistoryItemResponse::from);}

}
