package com.example.aisocket.project.application.internal.token;

import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RefreshToken;

public interface RefreshTokenRegisterService {

    RefreshToken register(Member member, IssuedToken issuedToken);

    RefreshToken findUsable(String rawRefreshToken);

    RefreshToken revoke(String rawRefreshToken);

}
