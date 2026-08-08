package com.example.aisocket.project.domain;

import java.time.LocalDateTime;

public final class RunningWorkoutFixture {

    private static final Member DEFAULT_MEMBER = MemberFixture.builder().build();
    private static final AthleteTier DEFAULT_TIER = AthleteTier.AMATEUR;
    private static final LocalDateTime DEFAULT_STARTED_AT = LocalDateTime.of(2026, 7, 18, 7, 0);
    private static final LocalDateTime DEFAULT_ENDED_AT = LocalDateTime.of(2026, 7, 18, 7, 45);
    private static final Double DEFAULT_DISTANCE = 8.2;
    private static final Double DEFAULT_ELEV_GAIN = 120.0;
    private static final Double DEFAULT_ELEVATION_MAX = 85.0;
    private static final Integer DEFAULT_MOVING_TIME = 45;
    private static final Double DEFAULT_CALORIES = 530.0;
    private static final Double DEFAULT_AVG_CADENCE = 172.0;
    private static final Double DEFAULT_MAX_CADENCE = 188.0;
    private static final Double DEFAULT_MAX_HEART_RATE = 176.0;
    private static final Double DEFAULT_AVG_HEART_RATE = 148.0;
    private static final Double DEFAULT_AVG_PACE = 5.48;
    private static final Double DEFAULT_MAX_PACE = 4.92;
    private static final Integer DEFAULT_STEPS = 7600;

    private RunningWorkoutFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Member member = DEFAULT_MEMBER;
        private AthleteTier tier = DEFAULT_TIER;
        private LocalDateTime startedAt = DEFAULT_STARTED_AT;
        private LocalDateTime endedAt = DEFAULT_ENDED_AT;
        private Double distance = DEFAULT_DISTANCE;
        private Double elevGain = DEFAULT_ELEV_GAIN;
        private Double elevationMax = DEFAULT_ELEVATION_MAX;
        private Integer movingTime = DEFAULT_MOVING_TIME;
        private Double calories = DEFAULT_CALORIES;
        private Double avgCadence = DEFAULT_AVG_CADENCE;
        private Double maxCadence = DEFAULT_MAX_CADENCE;
        private Double maxHeartRate = DEFAULT_MAX_HEART_RATE;
        private Double avgHeartRate = DEFAULT_AVG_HEART_RATE;
        private Double avgPace = DEFAULT_AVG_PACE;
        private Double maxPace = DEFAULT_MAX_PACE;
        private Integer steps = DEFAULT_STEPS;
        private String title = null;
        private WorkoutInputSource inputSource = WorkoutInputSource.DIRECT_INPUT;

        private Builder() {
        }

        public Builder member(Member member) {
            this.member = member;
            return this;
        }

        public Builder tier(AthleteTier tier) {
            this.tier = tier;
            return this;
        }

        public Builder startedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder endedAt(LocalDateTime endedAt) {
            this.endedAt = endedAt;
            return this;
        }

        public Builder distance(Double distance) {
            this.distance = distance;
            return this;
        }

        public Builder elevGain(Double elevGain) {
            this.elevGain = elevGain;
            return this;
        }

        public Builder elevationMax(Double elevationMax) {
            this.elevationMax = elevationMax;
            return this;
        }

        public Builder movingTime(Integer movingTime) {
            this.movingTime = movingTime;
            return this;
        }

        public Builder calories(Double calories) {
            this.calories = calories;
            return this;
        }

        public Builder avgCadence(Double avgCadence) {
            this.avgCadence = avgCadence;
            return this;
        }

        public Builder maxCadence(Double maxCadence) {
            this.maxCadence = maxCadence;
            return this;
        }

        public Builder maxHeartRate(Double maxHeartRate) {
            this.maxHeartRate = maxHeartRate;
            return this;
        }

        public Builder avgHeartRate(Double avgHeartRate) {
            this.avgHeartRate = avgHeartRate;
            return this;
        }

        public Builder avgPace(Double avgPace) {
            this.avgPace = avgPace;
            return this;
        }

        public Builder maxPace(Double maxPace) {
            this.maxPace = maxPace;
            return this;
        }

        public Builder steps(Integer steps) {
            this.steps = steps;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder inputSource(WorkoutInputSource inputSource) {
            this.inputSource = inputSource;
            return this;
        }

        public RunningWorkout build() {
            return RunningWorkout.create(member, tier, title, inputSource, commonCommand(), runningCommand());
        }

        public CreateCommonWorkoutCommand commonCommand() {
            return new CreateCommonWorkoutCommand(
                    startedAt,
                    endedAt,
                    distance,
                    elevGain,
                    elevationMax,
                    movingTime,
                    calories,
                    avgCadence,
                    maxCadence,
                    maxHeartRate,
                    avgHeartRate
            );
        }

        public CreateRunningWorkoutCommand runningCommand() {
            return new CreateRunningWorkoutCommand(avgPace, maxPace, steps);
        }
    }
}
