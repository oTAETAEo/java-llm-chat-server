package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.dto.response.FitWorkoutPreviewResponse;
import com.example.aisocket.project.adapter.in.dto.response.FitWorkoutSaveResponse;
import com.example.aisocket.project.adapter.in.security.AuthenticationMember;
import com.example.aisocket.project.application.in.FitWorkoutUploadService;
import com.example.aisocket.project.domain.AthleteTier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts/fit")
@RequiredArgsConstructor
public class FitWorkoutUploadController {

    private final FitWorkoutUploadService fitWorkoutUploadService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FitWorkoutPreviewResponse upload(
            @AuthenticationMember Long memberId,
            @RequestParam(defaultValue = "AMATEUR") AthleteTier tier,
            @RequestPart MultipartFile file
    ) {
        return FitWorkoutPreviewResponse.from(fitWorkoutUploadService.upload(memberId, tier, file));
    }

    @PostMapping("/records")
    @ResponseStatus(HttpStatus.CREATED)
    public FitWorkoutSaveResponse uploadAll(
            @AuthenticationMember Long memberId,
            @RequestParam(defaultValue = "AMATEUR") AthleteTier tier,
            @RequestPart List<MultipartFile> files
    ) {
        return FitWorkoutSaveResponse.from(fitWorkoutUploadService.uploadAll(memberId, tier, files));
    }
}
