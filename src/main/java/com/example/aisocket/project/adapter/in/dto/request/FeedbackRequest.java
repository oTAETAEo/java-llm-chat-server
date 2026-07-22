package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateCyclingWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.WorkOutType;

import java.time.LocalDateTime;

public record FeedbackRequest(

        WorkOutType workOutType,
        AthleteTier tier,

        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Double distance,
        Double elevGain,
        Double elevationMax,
        Integer movingTime,
        Double calories,
        Double avgCadence,
        Double maxCadence,
        Double maxHeartRate,
        Double avgHeartRate,

        // 자전거 전용 지표
        Double avgSpeed,
        Double maxSpeed,
        Double avgPower,
        Double maxPower,
        Double ftp,

        // 러닝 전용 지표
        Double avgPace,
        Double maxPace,
        Integer steps
) {

    public CoachFeedbackCommand toCommand() {
        return new CoachFeedbackCommand(
                workOutType,
                tier,
                toCommonCommand(),
                toRunningCommand(),
                toCyclingCommand()
        );
    }

    public CreateCommonWorkoutCommand toCommonCommand() {
        return new CreateCommonWorkoutCommand(
                startedAt, endedAt, distance, elevGain, elevationMax, movingTime, calories,
                avgCadence, maxCadence, maxHeartRate, avgHeartRate
        );
    }

    public CreateCyclingWorkoutCommand toCyclingCommand() {
        return new CreateCyclingWorkoutCommand(
                avgSpeed, maxSpeed, avgPower, maxPower, ftp
        );
    }

    public CreateRunningWorkoutCommand toRunningCommand() {
        return new CreateRunningWorkoutCommand(
                avgPace, maxPace, steps
        );
    }
}
