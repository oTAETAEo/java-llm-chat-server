package com.example.aisocket.project.application.service;

import com.example.aisocket.project.application.embedding.WorkoutEmbeddingContentBuilderRegistry;
import com.example.aisocket.project.application.out.EmbeddingGenerator;
import com.example.aisocket.project.application.out.WorkoutVectorRepository;
import com.example.aisocket.project.application.record.WorkoutRecordSaverRegistry;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateWorkoutVectorCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.Workout;
import com.example.aisocket.project.domain.WorkoutVector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutVectorSaveService {

    private final WorkoutRecordSaverRegistry workoutRecordSaverRegistry;

    private final WorkoutEmbeddingContentBuilderRegistry contentBuilderRegistry;

    private final EmbeddingGenerator embeddingGenerator;

    private final WorkoutVectorRepository workoutVectorRepository;

    public UUID save(Member member, Workout workout, AthleteTier tier) {

        Long workoutId = workoutRecordSaverRegistry.save(member, workout, tier);

        String content = contentBuilderRegistry.build(workout, tier);

        float[] embedding = embeddingGenerator.generate(content);

        WorkoutVector workoutVector = WorkoutVector.create(new CreateWorkoutVectorCommand(
                member.getId(),
                workoutId,
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
