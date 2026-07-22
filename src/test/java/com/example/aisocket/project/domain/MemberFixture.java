package com.example.aisocket.project.domain;

import com.example.aisocket.project.domain.security.TestPasswordHasher;

public final class MemberFixture {

    private static final Long DEFAULT_ID = 1L;
    private static final String DEFAULT_EMAIL = "runner@example.com";
    private static final String DEFAULT_RAW_PASSWORD = "raw-password";
    private static final String DEFAULT_ENCODED_PASSWORD = "encoded-password";
    private static final String DEFAULT_NICKNAME = "test-member";
    private static final TestPasswordHasher PASSWORD_HASHER = new TestPasswordHasher();

    private MemberFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id = DEFAULT_ID;
        private String email = DEFAULT_EMAIL;
        private String rawPassword = DEFAULT_RAW_PASSWORD;
        private String encodedPassword = DEFAULT_ENCODED_PASSWORD;
        private String nickname = DEFAULT_NICKNAME;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder rawPassword(String rawPassword) {
            this.rawPassword = rawPassword;
            return this;
        }

        public Builder encodedPassword(String encodedPassword) {
            this.encodedPassword = encodedPassword;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Member build() {
            return Member.of(id, email, encodedPassword, nickname);
        }

        public Member buildNew() {
            return Member.create(email, rawPassword, nickname, PASSWORD_HASHER);
        }
    }
}
