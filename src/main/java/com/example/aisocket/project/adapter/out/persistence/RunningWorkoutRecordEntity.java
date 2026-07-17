package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "running_workout_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RunningWorkoutRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AthleteTier tier;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

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

    public static RunningWorkoutRecordEntity from(RunningWorkout workout, AthleteTier tier, Member member) {
        return new RunningWorkoutRecordEntity(
                null,
                tier,
                MemberEntity.reference(member),
                workout.getDistance(),
                workout.getElevGain(),
                workout.getElevationMax(),
                workout.getMovingTime(),
                workout.getCalories(),
                workout.getAvgCadence(),
                workout.getMaxCadence(),
                workout.getMaxHeartRate(),
                workout.getAvgHeartRate(),
                workout.getAvgPace(),
                workout.getMaxPace(),
                workout.getSteps()
        );
    }
}
