package com.example.aisocket.project.adapter.in.security;

import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberUserDetailsTest {

    @Test
    @DisplayName("회원 도메인을 스프링 시큐리티 UserDetails로 감싼다")
    void wrapMemberAsUserDetails() {
        Member member = MemberFixture.builder()
                .id(1L)
                .email("runner@example.com")
                .encodedPassword("encoded-password")
                .nickname("runner")
                .build();

        MemberUserDetails memberUserDetails = new MemberUserDetails(member);

        assertThat(memberUserDetails.memberId()).isEqualTo(1L);
        assertThat(memberUserDetails.email()).isEqualTo("runner@example.com");
        assertThat(memberUserDetails.nickname()).isEqualTo("runner");
        assertThat(memberUserDetails.getUsername()).isEqualTo("runner@example.com");
        assertThat(memberUserDetails.getPassword()).isEqualTo("encoded-password");
        assertThat(memberUserDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }
}
