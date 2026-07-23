package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.domain.Member;

public interface TokenProvider {

    IssuedToken issue(Member member);
}
