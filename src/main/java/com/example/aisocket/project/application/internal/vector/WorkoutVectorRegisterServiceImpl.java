package com.example.aisocket.project.application.internal.vector;

import com.example.aisocket.project.application.internal.prompt.WorkoutEmbeddingPromptBuilder;
import com.example.aisocket.project.application.out.EmbeddingGenerator;
import com.example.aisocket.project.application.out.WorkoutVectorRepository;
import com.example.aisocket.project.application.internal.workout.WorkoutRecordRegistration;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateWorkoutVectorCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;
import com.example.aisocket.project.domain.WorkoutVector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutVectorRegisterServiceImpl implements WorkoutVectorRegisterService {

    private final WorkoutEmbeddingPromptBuilder workoutEmbeddingPromptBuilder;

    private final EmbeddingGenerator embeddingGenerator;

    private final WorkoutVectorRepository workoutVectorRepository;

    @Override
    @Transactional
    public UUID register(Member member, WorkoutRecordRegistration registration, AthleteTier tier) {
        Workout workout = registration.workout();

        String content = workoutEmbeddingPromptBuilder.build(workout, tier);

        float[] embedding = embeddingGenerator.generate(content);

        WorkoutVector workoutVector = WorkoutVector.create(new CreateWorkoutVectorCommand(
                member.getId(),
                registration.workoutId(),
                workout.getWorkOutType(),
                content,
                createMetadata(workout, tier),
                embedding
        ));

        return workoutVectorRepository.save(workoutVector);
    }

    private Map<String, Object> createMetadata(Workout workout, AthleteTier tier) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("tier", tier);
        metadata.put("workoutType", workout.getWorkOutType());
        metadata.put("startedAt", workout.getStartedAt());
        metadata.put("endedAt", workout.getEndedAt());
        metadata.put("distance", workout.getDistance());
        metadata.put("movingTime", workout.getMovingTime());
        metadata.put("calories", workout.getCalories());
        metadata.put("avgHeartRate", workout.getAvgHeartRate());
        metadata.put("maxHeartRate", workout.getMaxHeartRate());
        metadata.put("avgCadence", workout.getAvgCadence());
        metadata.put("maxCadence", workout.getMaxCadence());
        return metadata;
    }
}
