package com.example.aisocket.project.adapter.in.dto.request;

import com.example.aisocket.project.application.dto.command.CoachFeedbackCommand;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record FeedbackWithSensorRequest(
        FeedbackRequest workout,
        List<WorkoutSensorSampleRequest> samples
) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public CoachFeedbackCommand toCommand() {
        return workout.toCommand();
    }

    public CreateWorkoutSensorDataCommand toSensorCommand() {
        if (samples == null || samples.isEmpty()) {
            return null;
        }
        try {
            return new CreateWorkoutSensorDataCommand(OBJECT_MAPPER.writeValueAsString(samples));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("센서 샘플 JSON 변환에 실패했습니다.", exception);
        }
    }
}
