package com.example.aisocket.project.application.out;

import com.example.aisocket.project.JdbcTestSupport;
import com.example.aisocket.project.adapter.out.persistence.WorkoutVectorRepositoryAdapter;
import com.example.aisocket.project.domain.CreateWorkoutVectorCommand;
import com.example.aisocket.project.domain.WorkOutType;
import com.example.aisocket.project.domain.WorkoutVector;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(WorkoutVectorRepositoryAdapter.class)
class WorkoutVectorRepositoryTest extends JdbcTestSupport {

    @Autowired
    private WorkoutVectorRepository workoutVectorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("운동 벡터를 pgvector 저장소에 저장한다")
    void saveWorkoutVector() {
        WorkoutVector workoutVector = WorkoutVector.create(new CreateWorkoutVectorCommand(
                1L,
                10L,
                WorkOutType.RUNNING,
                "[러닝 운동 기록] 거리: 8.20 km",
                Map.of("workoutType", WorkOutType.RUNNING.name(), "distance", 8.2),
                testEmbedding()
        ));

        UUID vectorId = workoutVectorRepository.save(workoutVector);

        Map<String, Object> row = jdbcTemplate.queryForMap("""
                        SELECT member_id, workout_id, workout_type, content, metadata::text AS metadata
                        FROM workout_vector_store
                        WHERE id = CAST(? AS uuid)
                        """,
                vectorId.toString()
        );

        assertThat(row.get("member_id")).isEqualTo(1L);
        assertThat(row.get("workout_id")).isEqualTo(10L);
        assertThat(row.get("workout_type")).isEqualTo(WorkOutType.RUNNING.name());
        assertThat(row.get("content")).isEqualTo("[러닝 운동 기록] 거리: 8.20 km");
        assertThat(row.get("metadata").toString()).contains("\"workoutType\": \"RUNNING\"");
    }

    @Test
    @DisplayName("pgvector 컬럼 차원과 다른 임베딩을 저장하면 실패한다")
    void saveWorkoutVectorWithInvalidEmbeddingDimensionFails() {
        WorkoutVector workoutVector = WorkoutVector.create(new CreateWorkoutVectorCommand(
                1L,
                10L,
                WorkOutType.RUNNING,
                "[러닝 운동 기록] 거리: 8.20 km",
                Map.of("workoutType", WorkOutType.RUNNING.name()),
                new float[]{0.1f, 0.2f, 0.3f}
        ));

        assertThatThrownBy(() -> workoutVectorRepository.save(workoutVector))
                .isInstanceOf(DataAccessException.class);
    }

    private float[] testEmbedding() {
        float[] embedding = new float[1536];
        embedding[0] = 0.1f;
        embedding[1] = 0.2f;
        embedding[2] = 0.3f;
        return embedding;
    }

    @TestConfiguration
    static class ObjectMapperTestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
