package com.example.aisocket.project.application.internal.member;

import com.example.aisocket.project.application.dto.command.LoginCommand;
import com.example.aisocket.project.domain.Member;

public interface MemberFinderService {

    void validateNotExistsByEmail(String email);

    Member findLoginMember(LoginCommand command);
}
