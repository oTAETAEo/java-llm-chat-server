package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.result.FitWorkoutPreviewResult;
import com.example.aisocket.project.application.dto.result.FitWorkoutSaveResult;
import com.example.aisocket.project.application.in.FitWorkoutUploadService;
import com.example.aisocket.project.application.internal.fit.FitFileParser;
import com.example.aisocket.project.application.internal.fit.FitParseResult;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.internal.workout.WorkoutSensorDataRegisterService;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.WorkoutInputSource;
import com.example.aisocket.project.domain.WorkoutTitle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class FitWorkoutUploadServiceImpl implements FitWorkoutUploadService {

    private static final int MAX_UPLOAD_FILE_COUNT = 10;

    private final FitFileParser fitFileParser;

    private final WorkoutRecordRegisterService workoutRecordRegisterService;

    private final WorkoutSensorDataRegisterService workoutSensorDataRegisterService;

    private final ObjectMapper objectMapper;

    @Override
    public FitWorkoutPreviewResult upload(Long memberId, AthleteTier tier, MultipartFile file) {

        FitParseResult parseResult = fitFileParser.parse(file);

        return toResult(tier, parseResult);
    }

    @Override
    @Transactional
    public FitWorkoutSaveResult uploadAll(Long memberId, AthleteTier tier, List<MultipartFile> files) {
        validateFileCount(files);

        List<FitWorkoutSaveResult.SavedWorkoutResult> savedWorkouts = files.stream()
                .map(file -> save(memberId, tier, file))
                .toList();

        return FitWorkoutSaveResult.from(savedWorkouts);
    }

    private void validateFileCount(List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.size() > MAX_UPLOAD_FILE_COUNT) {
            throw new ProjectException(WorkoutErrorCode.FIT_FILE_COUNT_INVALID);
        }
    }

    private FitWorkoutSaveResult.SavedWorkoutResult save(Long memberId, AthleteTier tier, MultipartFile file) {
        FitParseResult parseResult = fitFileParser.parse(file);
        WorkoutRecordRegistration registration = workoutRecordRegisterService.register(memberId, parseResult.toCommand(tier));
        workoutSensorDataRegisterService.register(registration, toSensorCommand(parseResult));

        return new FitWorkoutSaveResult.SavedWorkoutResult(
                file.getOriginalFilename(),
                registration.workoutId(),
                parseResult.workOutType(),
                tier,
                WorkoutTitle.defaultTitle(parseResult.workOutType(), parseResult.distance()),
                WorkoutInputSource.FIT_FILE,
                registration.created(),
                parseResult.startedAt(),
                parseResult.endedAt(),
                parseResult.distance(),
                parseResult.movingTime()
        );
    }

    private CreateWorkoutSensorDataCommand toSensorCommand(FitParseResult parseResult) {
        if (parseResult.samples().isEmpty()) {
            return null;
        }

        try {
            return new CreateWorkoutSensorDataCommand(objectMapper.writeValueAsString(parseResult.samples()));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("FIT 센서 데이터를 JSON으로 변환할 수 없습니다.", exception);
        }
    }

    private FitWorkoutPreviewResult toResult(AthleteTier tier, FitParseResult parseResult) {
        return new FitWorkoutPreviewResult(
                parseResult.workOutType(),
                tier,
                WorkoutTitle.defaultTitle(parseResult.workOutType(), parseResult.distance()),
                WorkoutInputSource.FIT_FILE,
                parseResult.startedAt(),
                parseResult.endedAt(),
                parseResult.distance(),
                parseResult.elevGain(),
                parseResult.elevationMax(),
                parseResult.movingTime(),
                parseResult.calories(),
                parseResult.avgCadence(),
                parseResult.maxCadence(),
                parseResult.maxHeartRate(),
                parseResult.avgHeartRate(),
                parseResult.avgSpeed(),
                parseResult.maxSpeed(),
                parseResult.avgPower(),
                parseResult.maxPower(),
                parseResult.ftp(),
                parseResult.avgPace(),
                parseResult.maxPace(),
                parseResult.steps(),
                parseResult.samples().stream()
                        .map(sample -> new FitWorkoutPreviewResult.FitSensorSampleResult(
                                sample.elapsedSeconds(),
                                sample.distance(),
                                sample.latitude(),
                                sample.longitude(),
                                sample.altitude(),
                                sample.heartRate(),
                                sample.cadence(),
                                sample.speed(),
                                sample.power()
                        ))
                        .toList()
        );
    }
}
