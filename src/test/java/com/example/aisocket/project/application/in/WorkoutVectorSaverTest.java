package com.example.aisocket.project.application.in;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.out.EmbeddingGenerator;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.WorkOutType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class WorkoutVectorSaverTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private WorkoutVectorSaver workoutVectorSaver;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private EmbeddingGenerator embeddingGenerator;

    @Test
    @DisplayName("단일 러닝 운동 기록을 RDB와 pgvector 저장소에 함께 저장한다")
    void saveRunningWorkoutVector() {
        given(embeddingGenerator.generate(anyString())).willReturn(testEmbedding());

        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        RunningWorkout workout = runningWorkout();

        UUID vectorId = workoutVectorSaver.save(member, 10L, workout, AthleteTier.AMATEUR);

        Map<String, Object> vectorRow = jdbcTemplate.queryForMap("""
                        SELECT member_id, workout_id, workout_type, content, metadata::text AS metadata
                        FROM workout_vector_store
                        WHERE id = CAST(? AS uuid)
                        """,
                vectorId.toString()
        );

        assertThat(vectorRow.get("member_id")).isEqualTo(member.getId());
        assertThat(vectorRow.get("workout_type")).isEqualTo(WorkOutType.RUNNING.name());
        assertThat(vectorRow.get("workout_id")).isEqualTo(10L);
        assertThat(vectorRow.get("content").toString()).contains("[러닝 운동 기록]", "거리: 8.20 km");
        assertThat(vectorRow.get("metadata").toString()).contains("\"workoutType\": \"RUNNING\"");
    }

    @Test
    @DisplayName("임베딩 생성에 실패하면 벡터 저장 없이 예외를 전파한다")
    void saveRunningWorkoutVectorWhenEmbeddingGenerationFails() {
        RuntimeException embeddingException = new RuntimeException("embedding failed");
        given(embeddingGenerator.generate(anyString())).willThrow(embeddingException);
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        RunningWorkout workout = runningWorkout();

        assertThatThrownBy(() -> workoutVectorSaver.save(member, 10L, workout, AthleteTier.AMATEUR))
                .isSameAs(embeddingException);

        assertThat(countRows("workout_vector_store")).isZero();
        verify(embeddingGenerator).generate(anyString());
    }

    @Test
    @DisplayName("벡터 저장에 실패하면 예외를 상위로 전파한다")
    void saveRunningWorkoutVectorWhenVectorStoreFails() {
        given(embeddingGenerator.generate(anyString())).willReturn(new float[]{0.1f, 0.2f, 0.3f});
        Member member = MemberFixture.builder().id(1L).nickname("runner").build();
        RunningWorkout workout = runningWorkout();

        assertThatThrownBy(() -> workoutVectorSaver.save(member, 10L, workout, AthleteTier.AMATEUR))
                .isInstanceOf(DataAccessException.class);

        verify(embeddingGenerator).generate(anyString());
    }

    private RunningWorkout runningWorkout() {
        return RunningWorkout.create(
                MemberFixture.builder().build(),
                AthleteTier.AMATEUR,
                new CreateCommonWorkoutCommand(
                        LocalDateTime.of(2026, 7, 18, 7, 0),
                        LocalDateTime.of(2026, 7, 18, 7, 45),
                        8.2,
                        120.0,
                        85.0,
                        45,
                        530.0,
                        172.0,
                        188.0,
                        176.0,
                        148.0
                ),
                new CreateRunningWorkoutCommand(
                        5.48,
                        4.92,
                        7600
                )
        );
    }

    private Long countRows(String tableName) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Long.class);
    }

    private float[] testEmbedding() {
        float[] embedding = new float[1536];
        embedding[0] = 0.1f;
        embedding[1] = 0.2f;
        embedding[2] = 0.3f;
        return embedding;
    }



}
