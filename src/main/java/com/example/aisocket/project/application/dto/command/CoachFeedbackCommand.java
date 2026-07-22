package com.example.aisocket.project.application.dto.command;

import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateCyclingWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.WorkOutType;
import jakarta.validation.constraints.NotNull;

public record CoachFeedbackCommand(
        @NotNull(message = "운동 종목(workOutType)은 필수 값입니다.")
        WorkOutType workOutType,

        @NotNull(message = "운동 수준(tier)은 필수 값입니다.")
        AthleteTier tier,

        @NotNull(message = "공통 운동 데이터(commonCommand)는 필수 값입니다.")
        CreateCommonWorkoutCommand commonCommand,

        CreateRunningWorkoutCommand runningCommand,

        CreateCyclingWorkoutCommand cyclingCommand
) {
}
