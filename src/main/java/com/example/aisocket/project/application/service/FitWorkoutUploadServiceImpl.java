package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.dto.result.FitWorkoutPreviewResult;
import com.example.aisocket.project.application.in.FitWorkoutUploadService;
import com.example.aisocket.project.application.internal.fit.FitFileParser;
import com.example.aisocket.project.application.internal.fit.FitParseResult;
import com.example.aisocket.project.domain.AthleteTier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@Validated
@RequiredArgsConstructor
public class FitWorkoutUploadServiceImpl implements FitWorkoutUploadService {

    private final FitFileParser fitFileParser;

    @Override
    public FitWorkoutPreviewResult upload(Long memberId, AthleteTier tier, MultipartFile file) {

        FitParseResult parseResult = fitFileParser.parse(file);

        return toResult(tier, parseResult);
    }

    private FitWorkoutPreviewResult toResult(AthleteTier tier, FitParseResult parseResult) {
        return new FitWorkoutPreviewResult(
                parseResult.workOutType(),
                tier,
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
