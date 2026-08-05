package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.result.FitWorkoutPreviewResult;
import com.example.aisocket.project.domain.AthleteTier;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public interface FitWorkoutUploadService {

    FitWorkoutPreviewResult upload(
            @NotNull(message = "회원 ID(memberId)는 필수 값입니다.") Long memberId,
            @NotNull(message = "운동 수준(tier)은 필수 값입니다.") AthleteTier tier,
            @NotNull(message = "FIT 파일(file)은 필수 값입니다.") MultipartFile file
    );
}
