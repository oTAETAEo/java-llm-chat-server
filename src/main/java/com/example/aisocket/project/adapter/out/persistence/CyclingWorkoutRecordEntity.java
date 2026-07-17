package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.AthleteTier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("cycling_workout_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CyclingWorkoutRecordEntity {

    @Id
    private Long id;

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
