package com.example.aisocket.project.adapter.in.security;

import com.example.aisocket.project.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class MemberUserDetails implements UserDetails {

    private static final String ROLE_USER = "ROLE_USER";

    private final Member member;

    public Member member() {
        return member;
    }

    public Long memberId() {
        return member.getId();
    }

    public String email() {
        return member.getEmail();
    }

    public String nickname() {
        return member.getNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_USER));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }
}
