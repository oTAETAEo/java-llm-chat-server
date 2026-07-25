package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.domain.Member;

public interface JwtTokenProvider {

    IssuedToken issue(Member member);
}
