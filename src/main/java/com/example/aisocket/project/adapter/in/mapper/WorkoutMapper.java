package com.example.aisocket.project.adapter.in.mapper;

import com.example.aisocket.project.adapter.in.FeedbackRequest;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.Workout;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkoutMapper {

    private final Map<WorkOutType, WorkoutRequestMapper> mappers;

    public WorkoutMapper(List<WorkoutRequestMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(
                        WorkoutRequestMapper::supportType,
                        Function.identity()
                ));
    }

    public Workout toWorkout(FeedbackRequest request) {
        if (request.workOutType() == null) {
            throw new IllegalArgumentException("운동 종목(workOutType)은 필수 값입니다.");
        }

        WorkoutRequestMapper mapper = mappers.get(request.workOutType());
        if (mapper == null) {
            throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + request.workOutType());
        }

        return mapper.toWorkout(request);
    }
}
