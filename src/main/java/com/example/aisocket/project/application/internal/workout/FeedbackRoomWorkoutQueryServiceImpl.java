package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.application.dto.result.FeedbackRoomWorkoutResult;
import com.example.aisocket.project.application.out.CyclingWorkoutRepository;
import com.example.aisocket.project.application.out.CyclingWorkoutSensorDataRepository;
import com.example.aisocket.project.application.out.RunningWorkoutRepository;
import com.example.aisocket.project.application.out.RunningWorkoutSensorDataRepository;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.FeedbackRoomWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class FeedbackRoomWorkoutQueryServiceImpl implements FeedbackRoomWorkoutQueryService {

    private static final TypeReference<List<FeedbackRoomWorkoutResult.SensorSampleResult>> SENSOR_SAMPLE_TYPE =
            new TypeReference<>() {
            };

    private final ObjectMapper objectMapper;

    private final RunningWorkoutRepository runningWorkoutRepository;

    private final CyclingWorkoutRepository cyclingWorkoutRepository;

    private final RunningWorkoutSensorDataRepository runningWorkoutSensorDataRepository;

    private final CyclingWorkoutSensorDataRepository cyclingWorkoutSensorDataRepository;

    @Override
    public FeedbackRoomWorkoutResult findWorkout(Long memberId, FeedbackRoomWorkout roomWorkout) {
        if (roomWorkout.getWorkoutType() == WorkOutType.RUNNING) {
            return runningWorkoutRepository.findByIdAndMemberId(roomWorkout.getWorkoutId(), memberId)
                    .map(workout -> FeedbackRoomWorkoutResult.from(
                            workout,
                            runningWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workout.getId())
                                    .map(this::parseSamples)
                                    .orElseGet(List::of)
                    ))
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
        }

        if (roomWorkout.getWorkoutType() == WorkOutType.CYCLING) {
            return cyclingWorkoutRepository.findByIdAndMemberId(roomWorkout.getWorkoutId(), memberId)
                    .map(workout -> FeedbackRoomWorkoutResult.from(
                            workout,
                            cyclingWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workout.getId())
                                    .map(this::parseSamples)
                                    .orElseGet(List::of)
                    ))
                    .orElseThrow(() -> new ProjectException(WorkoutErrorCode.WORKOUT_NOT_FOUND));
        }

        throw new ProjectException(WorkoutErrorCode.UNSUPPORTED_WORKOUT_TYPE);
    }

    private List<FeedbackRoomWorkoutResult.SensorSampleResult> parseSamples(String samplesJson) {
        try {
            return objectMapper.readValue(samplesJson, SENSOR_SAMPLE_TYPE);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("저장된 센서 데이터 JSON을 읽을 수 없습니다.", exception);
        }
    }
}
