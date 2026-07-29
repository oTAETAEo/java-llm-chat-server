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

    private final Long memberId;

    private final String email;

    private final String password;

    private final String nickname;

    public MemberUserDetails(Member member) {
        this(member.getId(), member.getEmail(), member.getPassword(), member.getNickname());
    }

    public Long memberId() {
        return memberId;
    }

    public String email() {
        return email;
    }

    public String nickname() {
        return nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_USER));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
