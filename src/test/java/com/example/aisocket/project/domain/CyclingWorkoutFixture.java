package com.example.aisocket.project.domain;

import java.time.LocalDateTime;

public final class CyclingWorkoutFixture {

    private static final Member DEFAULT_MEMBER = MemberFixture.builder().build();
    private static final AthleteTier DEFAULT_TIER = AthleteTier.PRO;
    private static final LocalDateTime DEFAULT_STARTED_AT = LocalDateTime.of(2026, 7, 18, 9, 0);
    private static final LocalDateTime DEFAULT_ENDED_AT = LocalDateTime.of(2026, 7, 18, 10, 30);
    private static final Double DEFAULT_DISTANCE = 42.5;
    private static final Double DEFAULT_ELEV_GAIN = 650.0;
    private static final Double DEFAULT_ELEVATION_MAX = 240.0;
    private static final Integer DEFAULT_MOVING_TIME = 90;
    private static final Double DEFAULT_CALORIES = 920.0;
    private static final Double DEFAULT_AVG_CADENCE = 88.0;
    private static final Double DEFAULT_MAX_CADENCE = 104.0;
    private static final Double DEFAULT_MAX_HEART_RATE = 168.0;
    private static final Double DEFAULT_AVG_HEART_RATE = 142.0;
    private static final Double DEFAULT_AVG_SPEED = 27.4;
    private static final Double DEFAULT_MAX_SPEED = 44.1;
    private static final Double DEFAULT_AVG_POWER = 185.0;
    private static final Double DEFAULT_MAX_POWER = 420.0;
    private static final Double DEFAULT_FTP = 250.0;

    private CyclingWorkoutFixture() {
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
        private Double avgSpeed = DEFAULT_AVG_SPEED;
        private Double maxSpeed = DEFAULT_MAX_SPEED;
        private Double avgPower = DEFAULT_AVG_POWER;
        private Double maxPower = DEFAULT_MAX_POWER;
        private Double ftp = DEFAULT_FTP;
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

        public Builder avgSpeed(Double avgSpeed) {
            this.avgSpeed = avgSpeed;
            return this;
        }

        public Builder maxSpeed(Double maxSpeed) {
            this.maxSpeed = maxSpeed;
            return this;
        }

        public Builder avgPower(Double avgPower) {
            this.avgPower = avgPower;
            return this;
        }

        public Builder maxPower(Double maxPower) {
            this.maxPower = maxPower;
            return this;
        }

        public Builder ftp(Double ftp) {
            this.ftp = ftp;
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

        public CyclingWorkout build() {
            return CyclingWorkout.create(member, tier, title, inputSource, commonCommand(), cyclingCommand());
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

        public CreateCyclingWorkoutCommand cyclingCommand() {
            return new CreateCyclingWorkoutCommand(avgSpeed, maxSpeed, avgPower, maxPower, ftp);
        }
    }
}
