package com.example.aisocket.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackRoomTest {

    @Test
    @DisplayName("피드백 방을 생성한다")
    void createFeedbackRoom() {
        Member member = MemberFixture.builder().id(1L).build();

        FeedbackRoom room = FeedbackRoomFixture.builder()
                .member(member)
                .title(" 러닝 10km 피드백 ")
                .build();

        assertThat(room.getId()).isNotNull();
        assertThat(room.getMember()).isSameAs(member);
        assertThat(room.getTitle()).isEqualTo("러닝 10km 피드백");
        assertThat(room.isPinned()).isFalse();
    }

    @Test
    @DisplayName("피드백 방 제목이 없으면 기본 제목으로 생성한다")
    void createWithDefaultTitle() {
        FeedbackRoom room = FeedbackRoomFixture.builder()
                .title(" ")
                .build();

        assertThat(room.getTitle()).isEqualTo("새 운동 피드백");
    }

    @Test
    @DisplayName("피드백 방 제목은 최대 80자로 자른다")
    void truncateLongTitle() {
        String longTitle = "가".repeat(90);

        FeedbackRoom room = FeedbackRoomFixture.builder()
                .title(longTitle)
                .build();

        assertThat(room.getTitle()).hasSize(80);
        assertThat(room.getTitle()).isEqualTo("가".repeat(80));
    }

    @Test
    @DisplayName("피드백 방 제목을 변경한다")
    void renameFeedbackRoom() {
        FeedbackRoom room = FeedbackRoomFixture.builder().build();

        room.rename(" 자전거 피드백 ");

        assertThat(room.getTitle()).isEqualTo("자전거 피드백");
    }

    @Test
    @DisplayName("피드백 방 제목 변경 시 제목이 없으면 기본 제목으로 변경한다")
    void renameWithDefaultTitle() {
        FeedbackRoom room = FeedbackRoomFixture.builder().build();

        room.rename(null);

        assertThat(room.getTitle()).isEqualTo("새 운동 피드백");
    }

    @Test
    @DisplayName("피드백 방을 고정하고 고정 해제한다")
    void pinAndUnpinFeedbackRoom() {
        FeedbackRoom room = FeedbackRoomFixture.builder().build();

        room.pin();
        assertThat(room.isPinned()).isTrue();

        room.unpin();
        assertThat(room.isPinned()).isFalse();
    }

    @Test
    @DisplayName("피드백 방 소유자를 확인한다")
    void checkOwnership() {
        FeedbackRoom room = FeedbackRoomFixture.builder()
                .member(MemberFixture.builder().id(1L).build())
                .build();

        assertThat(room.isOwnedBy(1L)).isTrue();
        assertThat(room.isOwnedBy(2L)).isFalse();
        assertThat(room.isOwnedBy(null)).isFalse();
    }

    @Test
    @DisplayName("회원이 없으면 피드백 방 생성에 실패한다")
    void createWithoutMemberFails() {
        assertThatThrownBy(() -> FeedbackRoomFixture.builder()
                .member(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 ID가 없으면 피드백 방 생성에 실패한다")
    void createWithoutMemberIdFails() {
        assertThatThrownBy(() -> FeedbackRoomFixture.builder()
                .member(MemberFixture.builder().buildNew())
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
