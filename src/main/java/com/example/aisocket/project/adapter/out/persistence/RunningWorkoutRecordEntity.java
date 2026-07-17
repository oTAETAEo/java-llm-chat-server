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
@Table("running_workout_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RunningWorkoutRecordEntity {

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

    private Double avgPace;
    private Double maxPace;
    private Integer steps;

    public static RunningWorkoutRecordEntity from(FeedbackRequest request) {
        return new RunningWorkoutRecordEntity(
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
                request.avgPace(),
                request.maxPace(),
                request.steps()
        );
    }
}
