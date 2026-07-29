package com.example.aisocket.project.domain;

public final class FeedbackMessageFixture {

    private static final FeedbackRoom DEFAULT_ROOM = FeedbackRoomFixture.builder().build();
    private static final WorkOutType DEFAULT_WORKOUT_TYPE = WorkOutType.RUNNING;
    private static final Long DEFAULT_WORKOUT_ID = 10L;
    private static final String DEFAULT_CONTENT = "운동 피드백 메시지";

    private FeedbackMessageFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private FeedbackRoom room = DEFAULT_ROOM;
        private WorkOutType workoutType = DEFAULT_WORKOUT_TYPE;
        private Long workoutId = DEFAULT_WORKOUT_ID;
        private String content = DEFAULT_CONTENT;

        private Builder() {
        }

        public Builder room(FeedbackRoom room) {
            this.room = room;
            return this;
        }

        public Builder workoutType(WorkOutType workoutType) {
            this.workoutType = workoutType;
            return this;
        }

        public Builder workoutId(Long workoutId) {
            this.workoutId = workoutId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public FeedbackMessage buildUserWorkoutMessage() {
            return FeedbackMessage.createUserWorkoutMessage(room, workoutType, workoutId, content);
        }

        public FeedbackMessage buildAssistantMessage() {
            return FeedbackMessage.createAssistantMessage(room, workoutType, workoutId, content);
        }
    }
}
