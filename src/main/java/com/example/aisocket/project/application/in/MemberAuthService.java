package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.application.dto.command.LogoutCommand;
import com.example.aisocket.project.application.dto.command.ReissueTokenCommand;
import com.example.aisocket.project.application.dto.command.SignUpMemberCommand;
import com.example.aisocket.project.application.dto.result.LoginResult;
import com.example.aisocket.project.application.dto.result.LogoutResult;
import com.example.aisocket.project.application.dto.result.ReissueTokenResult;
import com.example.aisocket.project.application.dto.result.SignUpMemberResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface MemberAuthService {

    SignUpMemberResult signUp(
            @NotNull(message = "회원가입 요청(command)은 필수 값입니다.") @Valid SignUpMemberCommand command
    );

    LoginResult login(
            @NotNull(message = "로그인 요청(command)은 필수 값입니다.") @Valid LoginCommand command
    );

    ReissueTokenResult reissueToken(
            @NotNull(message = "토큰 재발급 요청(command)은 필수 값입니다.") @Valid ReissueTokenCommand command
    );

    LogoutResult logout(
            @NotNull(message = "로그아웃 요청(command)은 필수 값입니다.") @Valid LogoutCommand command
    );
}
