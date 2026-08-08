package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.out.CyclingWorkoutSensorDataRepository;
import com.example.aisocket.project.application.out.RunningWorkoutSensorDataRepository;
import com.example.aisocket.project.domain.WorkOutType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class WorkoutSensorDataQueryServiceImpl implements WorkoutSensorDataQueryService {

    private static final TypeReference<List<FeedbackRoomWorkoutResult.SensorSampleResult>> SENSOR_SAMPLE_TYPE =
            new TypeReference<>() {
            };

    private final ObjectMapper objectMapper;

    private final RunningWorkoutSensorDataRepository runningWorkoutSensorDataRepository;

    private final CyclingWorkoutSensorDataRepository cyclingWorkoutSensorDataRepository;

    @Override
    public List<FeedbackRoomWorkoutResult.SensorSampleResult> findSamples(WorkOutType workOutType, Long workoutId) {
        Optional<String> samplesJson = switch (workOutType) {
            case RUNNING -> runningWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workoutId);
            case CYCLING -> cyclingWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workoutId);
        };

        return samplesJson.map(this::parseSamples).orElseGet(List::of);
    }

    private List<FeedbackRoomWorkoutResult.SensorSampleResult> parseSamples(String samplesJson) {
        try {
            return objectMapper.readValue(samplesJson, SENSOR_SAMPLE_TYPE);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("저장된 센서 데이터 JSON을 읽을 수 없습니다.", exception);
        }
    }
}
