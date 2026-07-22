package com.example.aisocket.project.application.internal.member;

import com.example.aisocket.project.application.dto.command.RegisterMemberCommand;
import com.example.aisocket.project.domain.Member;

public interface MemberRegisterService {

    Member register(RegisterMemberCommand command);
}
