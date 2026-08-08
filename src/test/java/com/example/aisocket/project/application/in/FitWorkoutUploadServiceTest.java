package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.dto.result.FitWorkoutPreviewResult;
import com.example.aisocket.project.application.dto.result.FitWorkoutSaveResult;
import com.example.aisocket.project.application.internal.fit.FitFileParser;
import com.example.aisocket.project.application.internal.fit.FitParseResult;
import com.example.aisocket.project.application.internal.vector.WorkoutVectorRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegisterService;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.application.internal.workout.WorkoutSensorDataRegisterService;
import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutInputSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @MockitoBean
    private WorkoutSensorDataRegisterService workoutSensorDataRegisterService;

    @Test
    @DisplayName("FIT 파일을 파싱해 운동 미리보기 데이터를 반환하고 DB에는 저장하지 않는다")
    void upload() {
        MockMultipartFile file = new MockMultipartFile("file", "activity.fit", "application/octet-stream", new byte[]{1, 2, 3});
        FitParseResult parseResult = runningParseResult();

        given(fitFileParser.parse(file)).willReturn(parseResult);

        FitWorkoutPreviewResult result = fitWorkoutUploadService.upload(1L, AthleteTier.AMATEUR, file);

        verify(fitFileParser).parse(file);
        verify(workoutRecordRegisterService, never()).register(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(workoutVectorRegisterService, workoutSensorDataRegisterService);

        assertThat(result.workOutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(result.tier()).isEqualTo(AthleteTier.AMATEUR);
        assertThat(result.distance()).isEqualTo(10.0);
        assertThat(result.avgPace()).isEqualTo(300.0);
        assertThat(result.samples()).hasSize(1);
        assertThat(result.samples().get(0).heartRate()).isEqualTo(150);
    }

    @Test
    @DisplayName("FIT 파일 목록을 파싱해 운동 기록으로 저장하고 벡터는 생성하지 않는다")
    void uploadAll() {
        Member member = MemberFixture.builder().id(1L).build();
        MockMultipartFile firstFile = new MockMultipartFile("files", "morning.fit", "application/octet-stream", new byte[]{1, 2, 3});
        MockMultipartFile secondFile = new MockMultipartFile("files", "evening.fit", "application/octet-stream", new byte[]{4, 5, 6});
        FitParseResult parseResult = runningParseResult();

        given(fitFileParser.parse(firstFile)).willReturn(parseResult);
        given(fitFileParser.parse(secondFile)).willReturn(parseResult);
        given(workoutRecordRegisterService.register(eq(member.getId()), any()))
                .willReturn(new WorkoutRecordRegistration(
                        10L,
                        member,
                        RunningWorkoutFixture.builder()
                                .member(member)
                                .inputSource(WorkoutInputSource.FIT_FILE)
                                .build(),
                        true
                ))
                .willReturn(new WorkoutRecordRegistration(
                        10L,
                        member,
                        RunningWorkoutFixture.builder()
                                .member(member)
                                .inputSource(WorkoutInputSource.FIT_FILE)
                                .build(),
                        false
                ));

        FitWorkoutSaveResult result = fitWorkoutUploadService.uploadAll(
                member.getId(),
                AthleteTier.AMATEUR,
                List.of(firstFile, secondFile)
        );

        verify(fitFileParser).parse(firstFile);
        verify(fitFileParser).parse(secondFile);
        verify(workoutRecordRegisterService, times(2)).register(eq(member.getId()), any());
        verify(workoutSensorDataRegisterService, times(2)).register(
                any(),
                argThat(command -> command.samplesJson().contains("\"heartRate\":150"))
        );
        verifyNoInteractions(workoutVectorRegisterService);

        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.createdCount()).isEqualTo(1);
        assertThat(result.duplicatedCount()).isEqualTo(1);
        assertThat(result.items()).extracting(FitWorkoutSaveResult.SavedWorkoutResult::fileName)
                .containsExactly("morning.fit", "evening.fit");
        assertThat(result.items().get(0).workoutId()).isEqualTo(10L);
        assertThat(result.items().get(0).title()).isEqualTo("러닝 10.0km");
        assertThat(result.items().get(0).inputSource()).isEqualTo(WorkoutInputSource.FIT_FILE);
    }

    @Test
    @DisplayName("FIT 파일 목록에 센서 샘플이 없으면 센서 데이터 저장 명령을 넘기지 않는다")
    void uploadAllWithoutSensorSamples() {
        Member member = MemberFixture.builder().id(1L).build();
        MockMultipartFile file = new MockMultipartFile("files", "empty-sensor.fit", "application/octet-stream", new byte[]{1, 2, 3});
        FitParseResult parseResult = runningParseResultWithoutSamples();
        WorkoutRecordRegistration registration = new WorkoutRecordRegistration(
                10L,
                member,
                RunningWorkoutFixture.builder()
                        .member(member)
                        .inputSource(WorkoutInputSource.FIT_FILE)
                        .build(),
                true
        );

        given(fitFileParser.parse(file)).willReturn(parseResult);
        given(workoutRecordRegisterService.register(eq(member.getId()), any()))
                .willReturn(registration);

        fitWorkoutUploadService.uploadAll(member.getId(), AthleteTier.AMATEUR, List.of(file));

        verify(workoutSensorDataRegisterService).register(eq(registration), isNull());
    }

    @Test
    @DisplayName("FIT 파일 목록은 한 번에 최대 10개까지만 저장할 수 있다")
    void uploadAllWithTooManyFiles() {
        List<MultipartFile> files = java.util.stream.IntStream.range(0, 11)
                .<MultipartFile>mapToObj(index -> new MockMultipartFile("files", "activity-%d.fit".formatted(index), "application/octet-stream", new byte[]{1}))
                .toList();

        assertThatThrownBy(() -> fitWorkoutUploadService.uploadAll(1L, AthleteTier.AMATEUR, files))
                .isInstanceOf(ProjectException.class)
                .hasMessage("FIT 파일은 한 번에 1개 이상 10개 이하로 업로드할 수 있습니다.");

        verify(workoutRecordRegisterService, never()).register(any(), any());
        verifyNoInteractions(workoutVectorRegisterService);
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

    private FitParseResult runningParseResultWithoutSamples() {
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
                List.of()
        );
    }
}
