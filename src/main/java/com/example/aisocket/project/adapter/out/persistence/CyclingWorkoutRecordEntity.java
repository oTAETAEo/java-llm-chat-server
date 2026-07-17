package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.AthleteTier;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cycling_workout_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CyclingWorkoutRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AthleteTier tier;

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

    public static CyclingWorkoutRecordEntity from(FeedbackRequest request) {
        return new CyclingWorkoutRecordEntity(
                null,
                request.tier(),
                request.distance(),
                request.elevGain(),
                request.elevationMax(),
                request.movingTime(),
                request.calories(),
                request.avgCadence(),
                request.maxCadence(),
                request.maxHeartRate(),
                request.avgHeartRate(),
                request.avgSpeed(),
                request.maxSpeed(),
                request.avgPower(),
                request.maxPower(),
                request.ftp()
        );
    }
}
