package com.example.aisocket.project.domain;

public final class FeedbackRoomFixture {

    private static final Member DEFAULT_MEMBER = MemberFixture.builder().build();
    private static final String DEFAULT_TITLE = "러닝 피드백";

    private FeedbackRoomFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Member member = DEFAULT_MEMBER;
        private String title = DEFAULT_TITLE;

        private Builder() {
        }

        public Builder member(Member member) {
            this.member = member;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public FeedbackRoom build() {
            return FeedbackRoom.create(member, title);
        }
    }
}
