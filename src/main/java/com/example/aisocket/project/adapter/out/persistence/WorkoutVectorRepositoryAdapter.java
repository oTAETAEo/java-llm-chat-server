package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.application.out.WorkoutVectorRepository;
import com.example.aisocket.project.domain.WorkoutVector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class WorkoutVectorRepositoryAdapter implements WorkoutVectorRepository {

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    @PostConstruct
    void initializeSchema() {
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS workout_vector_store (
                    id uuid PRIMARY KEY,
                    member_id bigint,
                    workout_id bigint NOT NULL,
                    workout_type varchar(30) NOT NULL,
                    content text NOT NULL,
                    metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
                    embedding vector(1536) NOT NULL,
                    created_at timestamp NOT NULL
                )
                """);
    }

    @Override
    public UUID save(WorkoutVector workoutVector) {
        jdbcTemplate.update("""
                        INSERT INTO workout_vector_store (
                            id,
                            member_id,
                            workout_id,
                            workout_type,
                            content,
                            metadata,
                            embedding,
                            created_at
                        )
                        VALUES (
                            CAST(? AS uuid),
                            ?,
                            ?,
                            ?,
                            ?,
                            CAST(? AS jsonb),
                            CAST(? AS vector),
                            ?
                        )
                        """,
                workoutVector.getId().toString(),
                workoutVector.getMemberId(),
                workoutVector.getWorkoutId(),
                workoutVector.getWorkoutType().name(),
                workoutVector.getContent(),
                toMetadataJson(workoutVector),
                toVectorLiteral(workoutVector),
                workoutVector.getCreatedAt()
        );

        return workoutVector.getId();
    }

    private String toMetadataJson(WorkoutVector workoutVector) {
        try {
            return objectMapper.writeValueAsString(workoutVector.getMetadata());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("운동 벡터 metadata JSON 변환에 실패했습니다.", e);
        }
    }

    private String toVectorLiteral(WorkoutVector workoutVector) {
        float[] embedding = workoutVector.getEmbedding();

        return IntStream.range(0, embedding.length)
                .mapToObj(i -> Float.toString(embedding[i]))
                .collect(Collectors.joining(",", "[", "]"));
    }
}
