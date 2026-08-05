package com.example.aisocket.project.application.internal.fit;

import com.example.aisocket.project.common.error.ProjectException;
import com.example.aisocket.project.common.error.WorkoutErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GarminFitFileParserTest {

    private final GarminFitFileParser parser = new GarminFitFileParser();

    @Test
    @DisplayName("빈 FIT 파일은 거절한다")
    void parseEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.fit", "application/octet-stream", new byte[0]);

        assertThatThrownBy(() -> parser.parse(file))
                .isInstanceOfSatisfying(ProjectException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(WorkoutErrorCode.FIT_FILE_EMPTY));
    }

    @Test
    @DisplayName("FIT 확장자가 아니면 거절한다")
    void parseNonFitExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "activity.txt", "text/plain", "not-fit".getBytes());

        assertThatThrownBy(() -> parser.parse(file))
                .isInstanceOfSatisfying(ProjectException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(WorkoutErrorCode.INVALID_FIT_FILE));
    }

    @Test
    @DisplayName("FIT 시그니처가 없으면 거절한다")
    void parseInvalidFitSignature() {
        MockMultipartFile file = new MockMultipartFile("file", "activity.fit", "application/octet-stream", new byte[12]);

        assertThatThrownBy(() -> parser.parse(file))
                .isInstanceOfSatisfying(ProjectException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(WorkoutErrorCode.INVALID_FIT_FILE));
    }
}
