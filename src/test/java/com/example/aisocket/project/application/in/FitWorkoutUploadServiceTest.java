package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.result.FitWorkoutPreviewResult;
import com.example.aisocket.project.application.internal.fit.FitFileParser;
import com.example.aisocket.project.application.internal.fit.FitParseResult;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.WorkOutType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class FitWorkoutUploadServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private FitWorkoutUploadService fitWorkoutUploadService;

    @MockitoBean
    private FitFileParser fitFileParser;

    @MockitoBean
    private WorkoutRecordRegisterService workoutRecordRegisterService;

    @MockitoBean
    private WorkoutVectorRegisterService workoutVectorRegisterService;

    @Test
    @DisplayName("FIT 파일을 파싱해 운동 미리보기 데이터를 반환하고 DB에는 저장하지 않는다")
    void upload() {
        MockMultipartFile file = new MockMultipartFile("file", "activity.fit", "application/octet-stream", new byte[]{1, 2, 3});
        FitParseResult parseResult = runningParseResult();

        given(fitFileParser.parse(file)).willReturn(parseResult);

        FitWorkoutPreviewResult result = fitWorkoutUploadService.upload(1L, AthleteTier.AMATEUR, file);

        verify(fitFileParser).parse(file);
        verify(workoutRecordRegisterService, never()).register(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(workoutVectorRegisterService);

        assertThat(result.workOutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(result.tier()).isEqualTo(AthleteTier.AMATEUR);
        assertThat(result.distance()).isEqualTo(10.0);
        assertThat(result.avgPace()).isEqualTo(300.0);
        assertThat(result.samples()).hasSize(1);
        assertThat(result.samples().get(0).heartRate()).isEqualTo(150);
    }

    private FitParseResult runningParseResult() {
        return new FitParseResult(
                WorkOutType.RUNNING,
                LocalDateTime.parse("2026-08-05T00:00:00"),
                LocalDateTime.parse("2026-08-05T00:50:00"),
                10.0,
                100.0,
                250.0,
                3000,
                650.0,
                170.0,
                190.0,
                180.0,
                150.0,
                null,
                null,
                null,
                null,
                null,
                300.0,
                null,
                9000,
                List.of(new FitParseResult.FitSensorSample(
                        0,
                        0.0,
                        37.1,
                        127.1,
                        50.0,
                        150,
                        170,
                        12.5,
                        null
                ))
        );
    }
}
