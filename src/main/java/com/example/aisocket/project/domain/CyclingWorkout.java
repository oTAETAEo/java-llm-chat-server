package com.example.aisocket.project.domain;

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
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private AthleteTier tier;

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
            CreateCommonWorkoutCommand workoutCommand,
            CreateCyclingWorkoutCommand cycleWorkoutCommand
    ){
        return new CyclingWorkout(member, tier, workoutCommand, cycleWorkoutCommand);
    }

    private CyclingWorkout(Member member, AthleteTier tier, CreateCommonWorkoutCommand workoutCommand, CreateCyclingWorkoutCommand cycleWorkoutCommand) {
        this.tier = tier;
        this.member = member;

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
    }

    @Override
    public WorkOutType getWorkOutType() {
        return WorkOutType.CYCLING;
    }
}
