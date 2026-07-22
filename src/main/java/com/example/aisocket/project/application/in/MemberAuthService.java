package com.example.aisocket.project.application.in;

import com.example.aisocket.project.application.dto.command.RegisterMemberCommand;
import com.example.aisocket.project.application.dto.result.RegisterMemberResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface MemberAuthService {

    RegisterMemberResult register(
            @NotNull(message = "회원가입 요청(command)은 필수 값입니다.") @Valid RegisterMemberCommand command
    );
}
