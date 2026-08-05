package com.example.aisocket.project.adapter.in.dto.request;

public record WorkoutSensorSampleRequest(
        Integer elapsedSeconds,
        Double distance,
        Double latitude,
        Double longitude,
        Double altitude,
        Integer heartRate,
        Integer cadence,
        Double speed,
        Integer power
) {
}
