package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.command.WorkoutDashboardFilterCommand;
import com.example.aisocket.project.application.dto.result.CursorPageResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardDistanceResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.FeedbackUsageResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutDayFrequencyResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutFrequencyResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutTypeDistributionResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardInsightResult.WorkoutTypeShareResult;
import com.example.aisocket.project.application.dto.result.WorkoutDashboardSummaryResult;
import com.example.aisocket.project.application.dto.result.WorkoutHistoryItemResult;
import com.example.aisocket.project.application.in.WorkoutDashboardService;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Validated
@RequiredArgsConstructor
public class WorkoutDashboardServiceImpl implements WorkoutDashboardService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final RunningWorkoutRepository runningWorkoutRepository;
    private final CyclingWorkoutRepository cyclingWorkoutRepository;

    @Override
    public WorkoutDashboardSummaryResult getSummary(Long memberId, WorkoutDashboardFilterCommand filter) {
        DashboardWorkouts workouts = loadDashboardWorkouts(memberId, filter);
        List<WorkoutHistoryItemResult> histories = workouts.histories();

        double totalDistance = histories.stream().mapToDouble(item -> valueOrZero(item.distance())).sum();
        long totalMovingTime = histories.stream().mapToLong(item -> item.movingTime() == null ? 0L : item.movingTime()).sum();
        long totalFeedbackCount = histories.stream().mapToLong(item -> item.feedbackCount() == null ? 0L : item.feedbackCount()).sum();
        double totalElevGain = histories.stream().mapToDouble(item -> valueOrZero(item.elevGain())).sum();
        double runningDistance = workouts.runningWorkouts().stream().mapToDouble(workout -> valueOrZero(workout.getDistance())).sum();
        double cyclingDistance = workouts.cyclingWorkouts().stream().mapToDouble(workout -> valueOrZero(workout.getDistance())).sum();

        return new WorkoutDashboardSummaryResult(
                histories.size(),
                totalDistance,
                totalMovingTime,
                totalFeedbackCount,
                workouts.runningWorkouts().size(),
                workouts.cyclingWorkouts().size(),
                runningDistance,
                cyclingDistance,
                average(histories.stream().map(WorkoutHistoryItemResult::avgHeartRate)),
                totalElevGain,
                average(workouts.runningWorkouts().stream().map(RunningWorkout::getAvgPace)),
                average(workouts.cyclingWorkouts().stream().map(CyclingWorkout::getAvgPower)),
                histories.stream()
                        .limit(8)
                        .map(item -> new WorkoutDashboardDistanceResult(
                                item.title(),
                                item.startedAt(),
                                item.distance(),
                                item.movingTime(),
                                item.avgHeartRate(),
                                item.elevGain()
                        ))
                        .toList()
        );
    }

    @Override
    public WorkoutDashboardInsightResult getInsights(Long memberId, WorkoutDashboardFilterCommand filter) {
        DashboardWorkouts workouts = loadDashboardWorkouts(memberId, filter);
        List<WorkoutHistoryItemResult> histories = workouts.histories();

        long totalWorkoutCount = histories.size();
        double totalDistance = histories.stream().mapToDouble(item -> valueOrZero(item.distance())).sum();
        long runningCount = workouts.runningWorkouts().size();
        long cyclingCount = workouts.cyclingWorkouts().size();
        double runningDistance = workouts.runningWorkouts().stream().mapToDouble(workout -> valueOrZero(workout.getDistance())).sum();
        double cyclingDistance = workouts.cyclingWorkouts().stream().mapToDouble(workout -> valueOrZero(workout.getDistance())).sum();
        long feedbackUsedWorkoutCount = histories.stream()
                .filter(item -> item.feedbackCount() != null && item.feedbackCount() > 0)
                .count();
        long totalFeedbackCount = histories.stream()
                .mapToLong(item -> item.feedbackCount() == null ? 0L : item.feedbackCount())
                .sum();

        return new WorkoutDashboardInsightResult(
                new WorkoutTypeDistributionResult(
                        totalWorkoutCount,
                        totalDistance,
                        new WorkoutTypeShareResult(
                                WorkOutType.RUNNING,
                                runningCount,
                                runningDistance,
                                percent(runningCount, totalWorkoutCount),
                                percent(runningDistance, totalDistance)
                        ),
                        new WorkoutTypeShareResult(
                                WorkOutType.CYCLING,
                                cyclingCount,
                                cyclingDistance,
                                percent(cyclingCount, totalWorkoutCount),
                                percent(cyclingDistance, totalDistance)
                        ),
                        average(workouts.runningWorkouts().stream().map(RunningWorkout::getAvgPace)),
                        average(workouts.cyclingWorkouts().stream().map(CyclingWorkout::getAvgPower))
                ),
                buildWorkoutFrequency(histories),
                new FeedbackUsageResult(
                        totalWorkoutCount,
                        feedbackUsedWorkoutCount,
                        totalFeedbackCount,
                        percent(feedbackUsedWorkoutCount, totalWorkoutCount)
                )
        );
    }

    @Override
    public CursorPageResult<WorkoutHistoryItemResult> getHistories(
            Long memberId,
            WorkoutDashboardFilterCommand filter,
            String cursor,
            Integer size
    ) {
        WorkOutType filteredType = filter.filteredWorkoutType();
        LocalDateTime from = filter.startedAtFrom();
        LocalDateTime toExclusive = filter.startedAtToExclusive();
        WorkoutHistoryCursor parsedCursor = WorkoutHistoryCursor.parse(cursor);
        int pageSize = normalizeSize(size);
        int candidateSize = pageSize + 1;

        List<RunningWorkout> runningWorkouts = filteredType == WorkOutType.CYCLING
                ? List.of()
                : runningWorkoutRepository.findDashboardHistories(
                memberId,
                from,
                toExclusive,
                parsedCursor.startedAt(),
                parsedCursor.workoutId(),
                candidateSize
        );
        List<CyclingWorkout> cyclingWorkouts = filteredType == WorkOutType.RUNNING
                ? List.of()
                : cyclingWorkoutRepository.findDashboardHistories(
                memberId,
                from,
                toExclusive,
                parsedCursor.startedAt(),
                parsedCursor.workoutId(),
                candidateSize
        );
        List<WorkoutHistoryItemResult> candidates = mergeHistories(runningWorkouts, cyclingWorkouts);
        boolean hasNext = candidates.size() > pageSize;
        List<WorkoutHistoryItemResult> items = candidates.stream()
                .limit(pageSize)
                .toList();
        String nextCursor = hasNext && !items.isEmpty()
                ? WorkoutHistoryCursor.from(items.get(items.size() - 1)).value()
                : null;

        return new CursorPageResult<>(items, nextCursor, hasNext);
    }

    private DashboardWorkouts loadDashboardWorkouts(Long memberId, WorkoutDashboardFilterCommand filter) {
        WorkOutType filteredType = filter.filteredWorkoutType();
        LocalDateTime from = filter.startedAtFrom();
        LocalDateTime toExclusive = filter.startedAtToExclusive();

        List<RunningWorkout> runningWorkouts = filteredType == WorkOutType.CYCLING
                ? List.of()
                : runningWorkoutRepository.findDashboardWorkouts(memberId, from, toExclusive);
        List<CyclingWorkout> cyclingWorkouts = filteredType == WorkOutType.RUNNING
                ? List.of()
                : cyclingWorkoutRepository.findDashboardWorkouts(memberId, from, toExclusive);

        return new DashboardWorkouts(
                runningWorkouts,
                cyclingWorkouts,
                mergeHistories(runningWorkouts, cyclingWorkouts)
        );
    }

    private WorkoutFrequencyResult buildWorkoutFrequency(List<WorkoutHistoryItemResult> histories) {
        Map<DayOfWeek, Integer> counts = new EnumMap<>(DayOfWeek.class);
        Arrays.stream(DayOfWeek.values()).forEach(dayOfWeek -> counts.put(dayOfWeek, 0));

        histories.stream()
                .map(WorkoutHistoryItemResult::startedAt)
                .filter(Objects::nonNull)
                .map(LocalDateTime::getDayOfWeek)
                .forEach(dayOfWeek -> counts.compute(dayOfWeek, (key, count) -> count == null ? 1 : count + 1));

        List<WorkoutDayFrequencyResult> days = Arrays.stream(DayOfWeek.values())
                .map(dayOfWeek -> new WorkoutDayFrequencyResult(dayOfWeek, counts.get(dayOfWeek)))
                .toList();
        int maxCount = days.stream()
                .mapToInt(WorkoutDayFrequencyResult::count)
                .max()
                .orElse(0);

        return new WorkoutFrequencyResult(maxCount, days);
    }

    private List<WorkoutHistoryItemResult> mergeHistories(
            List<RunningWorkout> runningWorkouts,
            List<CyclingWorkout> cyclingWorkouts
    ) {
        return Stream.concat(
                        runningWorkouts.stream().map(WorkoutHistoryItemResult::from),
                        cyclingWorkouts.stream().map(WorkoutHistoryItemResult::from)
                )
                .sorted(historyComparator())
                .toList();
    }

    private Comparator<WorkoutHistoryItemResult> historyComparator() {
        return Comparator
                .comparing(WorkoutHistoryItemResult::startedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(WorkoutHistoryItemResult::workoutId, Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }

    private double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private Double average(Stream<Double> values) {
        List<Double> validValues = values
                .filter(Objects::nonNull)
                .filter(Double::isFinite)
                .toList();
        if (validValues.isEmpty()) {
            return null;
        }
        return validValues.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
    }

    private double percent(double numerator, double denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return numerator / denominator * 100.0;
    }

    private record DashboardWorkouts(
            List<RunningWorkout> runningWorkouts,
            List<CyclingWorkout> cyclingWorkouts,
            List<WorkoutHistoryItemResult> histories
    ) {
    }

    private record WorkoutHistoryCursor(LocalDateTime startedAt, Long workoutId) {

        static WorkoutHistoryCursor parse(String value) {
            if (value == null || value.isBlank()) {
                return new WorkoutHistoryCursor(null, null);
            }

            int separatorIndex = value.lastIndexOf("_");
            if (separatorIndex <= 0 || separatorIndex >= value.length() - 1) {
                throw new IllegalArgumentException("운동 기록 커서(cursor) 형식이 올바르지 않습니다.");
            }

            return new WorkoutHistoryCursor(
                    LocalDateTime.parse(value.substring(0, separatorIndex)),
                    Long.valueOf(value.substring(separatorIndex + 1))
            );
        }

        static WorkoutHistoryCursor from(WorkoutHistoryItemResult item) {
            return new WorkoutHistoryCursor(item.startedAt(), item.workoutId());
        }

        String value() {
            if (startedAt == null || workoutId == null) {
                return null;
            }
            return startedAt + "_" + workoutId;
        }
    }
}
