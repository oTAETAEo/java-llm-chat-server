package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackMessageTest {

    @Test
    @DisplayName("사용자 운동 메시지를 생성한다")
    void createUserWorkoutMessage() {
        FeedbackRoom room = FeedbackRoomFixture.builder().build();

        FeedbackMessage message = FeedbackMessageFixture.builder()
                .room(room)
                .workoutType(WorkOutType.RUNNING)
                .workoutId(10L)
                .content("러닝 운동 데이터")
                .buildUserWorkoutMessage();

        assertThat(message.getId()).isNull();
        assertThat(message.getRoom()).isSameAs(room);
        assertThat(message.getRole()).isEqualTo(FeedbackMessageRole.USER);
        assertThat(message.getWorkoutType()).isEqualTo(WorkOutType.RUNNING);
        assertThat(message.getWorkoutId()).isEqualTo(10L);
        assertThat(message.getContent()).isEqualTo("러닝 운동 데이터");
    }

    @Test
    @DisplayName("AI 피드백 메시지를 생성한다")
    void createAssistantMessage() {
        FeedbackRoom room = FeedbackRoomFixture.builder().build();

        FeedbackMessage message = FeedbackMessageFixture.builder()
                .room(room)
                .workoutType(WorkOutType.CYCLING)
                .workoutId(20L)
                .content("AI 운동 피드백")
                .buildAssistantMessage();

        assertThat(message.getRoom()).isSameAs(room);
        assertThat(message.getRole()).isEqualTo(FeedbackMessageRole.ASSISTANT);
        assertThat(message.getWorkoutType()).isEqualTo(WorkOutType.CYCLING);
        assertThat(message.getWorkoutId()).isEqualTo(20L);
        assertThat(message.getContent()).isEqualTo("AI 운동 피드백");
    }

    @Test
    @DisplayName("운동 참조 없이도 AI 피드백 메시지를 생성한다")
    void createAssistantMessageWithoutWorkoutReference() {
        FeedbackMessage message = FeedbackMessageFixture.builder()
                .workoutType(null)
                .workoutId(null)
                .buildAssistantMessage();

        assertThat(message.getWorkoutType()).isNull();
        assertThat(message.getWorkoutId()).isNull();
    }

    @Test
    @DisplayName("방이 없으면 피드백 메시지 생성에 실패한다")
    void createWithoutRoomFails() {
        assertThatThrownBy(() -> FeedbackMessageFixture.builder()
                .room(null)
                .buildUserWorkoutMessage())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("방 ID가 없으면 피드백 메시지 생성에 실패한다")
    void createWithoutRoomIdFails() {
        assertThatThrownBy(() -> FeedbackMessageFixture.builder()
                .room(null)
                .buildAssistantMessage())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("내용이 없으면 피드백 메시지 생성에 실패한다")
    void createWithoutContentFails() {
        assertThatThrownBy(() -> FeedbackMessageFixture.builder()
                .content(" ")
                .buildAssistantMessage())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
