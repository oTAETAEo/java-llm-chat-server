package com.example.aisocket.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "cycling_workout")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CyclingWorkout extends BaseEntity implements Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AthleteTier tier;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkoutInputSource inputSource;

    @Column(nullable = false)
    private Long feedbackCount = 0L;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Double distance;
    private Double elevGain;
    private Double elevationMax;
    private Integer movingTime;
    private Double calories;
    private Double avgCadence;
    private Double maxCadence;
    private Double maxHeartRate;
    private Double avgHeartRate;

    private Double avgSpeed;
    private Double maxSpeed;
    private Double avgPower;
    private Double maxPower;
    private Double ftp;

    public static CyclingWorkout create(
            Member member,
            AthleteTier tier,
            String title,
            WorkoutInputSource inputSource,
            CreateCommonWorkoutCommand workoutCommand,
            CreateCyclingWorkoutCommand cycleWorkoutCommand
    ){
        return new CyclingWorkout(member, tier, title, inputSource, workoutCommand, cycleWorkoutCommand);
    }

    private CyclingWorkout(
            Member member,
            AthleteTier tier,
            String title,
            WorkoutInputSource inputSource,
            CreateCommonWorkoutCommand workoutCommand,
            CreateCyclingWorkoutCommand cycleWorkoutCommand
    ) {
        this.tier = tier;
        this.member = member;
        this.inputSource = inputSource;
        this.feedbackCount = 0L;

        this.startedAt = workoutCommand.startedAt();
        this.endedAt = workoutCommand.endedAt();
        this.distance = workoutCommand.distance();
        this.elevGain = workoutCommand.elevGain();
        this.elevationMax = workoutCommand.elevationMax();
        this.movingTime = workoutCommand.movingTime();
        this.calories = workoutCommand.calories();
        this.avgCadence = workoutCommand.avgCadence();
        this.maxCadence = workoutCommand.maxCadence();
        this.maxHeartRate = workoutCommand.maxHeartRate();
        this.avgHeartRate = workoutCommand.avgHeartRate();

        this.avgSpeed = cycleWorkoutCommand.avgSpeed();
        this.maxSpeed = cycleWorkoutCommand.maxSpeed();
        this.avgPower = cycleWorkoutCommand.avgPower();
        this.maxPower = cycleWorkoutCommand.maxPower();
        this.ftp = cycleWorkoutCommand.ftp();
        this.title = normalizeTitleOrDefault(title);

        validate();
    }

    @Override
    public void validate() {
        validateRecordOwner();
        if (startedAt == null) {
            throw new IllegalArgumentException("운동 시작 시간(startedAt)은 필수 값입니다.");
        }
        if (endedAt == null) {
            throw new IllegalArgumentException("운동 종료 시간(endedAt)은 필수 값입니다.");
        }
        if (endedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("운동 종료 시간(endedAt)은 시작 시간(startedAt)보다 빠를 수 없습니다.");
        }
        if (movingTime != null && movingTime <= 0) {
            throw new IllegalArgumentException("운동 시간(movingTime)은 0보다 커야 합니다.");
        }
        if (distance != null && distance < 0) {
            throw new IllegalArgumentException("운동 거리(distance)는 음수가 될 수 없습니다.");
        }
        if (ftp != null && ftp < 0) {
            throw new IllegalArgumentException("자전거 FTP는 음수가 될 수 없습니다.");
        }
    }

    private void validateRecordOwner() {
        if (member == null) {
            throw new IllegalArgumentException("회원(member)은 운동 기록에 필수 값입니다.");
        }
        if (member.getId() == null) {
            throw new IllegalArgumentException("회원 ID(member.id)는 운동 기록에 필수 값입니다.");
        }
        if (tier == null) {
            throw new IllegalArgumentException("선수 등급(tier)은 운동 기록에 필수 값입니다.");
        }
        if (inputSource == null) {
            throw new IllegalArgumentException("운동 입력 출처(inputSource)는 필수 값입니다.");
        }
    }

    private String normalizeTitleOrDefault(String title) {
        return WorkoutTitle.normalizeOrDefault(title, WorkOutType.CYCLING, distance);
    }

    public Long getFeedbackCount() {
        return feedbackCount;
    }

    public void increaseFeedbackCount() {
        this.feedbackCount++;
    }

    public void updateMetadataFrom(CyclingWorkout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("갱신할 자전거 운동(workout)은 필수 값입니다.");
        }
        if (workout.getInputSource() == null) {
            throw new IllegalArgumentException("운동 입력 출처(inputSource)는 필수 값입니다.");
        }

        this.title = normalizeTitleOrDefault(workout.getTitle());
        this.inputSource = workout.getInputSource();
    }

    @Override
    public WorkOutType getWorkOutType() {
        return WorkOutType.CYCLING;
    }
}
