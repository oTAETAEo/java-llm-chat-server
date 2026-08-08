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
@Table(name = "running_workout")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningWorkout extends BaseEntity implements Workout {

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

    private Double avgPace;
    private Double maxPace;
    private Integer steps;

    public static RunningWorkout create(
            Member member,
            AthleteTier tier,
            String title,
            WorkoutInputSource inputSource,
            CreateCommonWorkoutCommand workoutCommand,
            CreateRunningWorkoutCommand runningWorkoutCommand
    ){
        return new RunningWorkout(member, tier, title, inputSource, workoutCommand, runningWorkoutCommand);
    }

    private RunningWorkout(
            Member member,
            AthleteTier tier,
            String title,
            WorkoutInputSource inputSource,
            CreateCommonWorkoutCommand workoutCommand,
            CreateRunningWorkoutCommand runningWorkoutCommand
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

        this.avgPace = runningWorkoutCommand.avgPace();
        this.maxPace = runningWorkoutCommand.maxPace();
        this.steps = runningWorkoutCommand.steps();
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
        if (steps != null && steps < 0) {
            throw new IllegalArgumentException("걸음 수(steps)는 음수가 될 수 없습니다.");
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
        return WorkoutTitle.normalizeOrDefault(title, WorkOutType.RUNNING, distance);
    }

    public Long getFeedbackCount() {
        return feedbackCount;
    }

    public void increaseFeedbackCount() {
        this.feedbackCount++;
    }

    @Override
    public WorkOutType getWorkOutType() {
        return WorkOutType.RUNNING;
    }
}
