package com.example.aisocket.project.domain;

public final class FeedbackRoomWorkoutFixture {

    private static final FeedbackRoom DEFAULT_ROOM = FeedbackRoomFixture.builder().build();
    private static final WorkOutType DEFAULT_WORKOUT_TYPE = WorkOutType.RUNNING;
    private static final Long DEFAULT_WORKOUT_ID = 10L;

    private FeedbackRoomWorkoutFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private FeedbackRoom room = DEFAULT_ROOM;
        private WorkOutType workoutType = DEFAULT_WORKOUT_TYPE;
        private Long workoutId = DEFAULT_WORKOUT_ID;

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

        public FeedbackRoomWorkout build() {
            return FeedbackRoomWorkout.create(room, workoutType, workoutId);
        }
    }
}
