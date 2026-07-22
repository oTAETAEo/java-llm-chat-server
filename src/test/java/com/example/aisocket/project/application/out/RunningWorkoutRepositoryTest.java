package com.example.aisocket.project.application.out;

import com.example.aisocket.project.DataJpaTestSupport;
import com.example.aisocket.project.adapter.out.persistence.MemberRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.RunningWorkoutRepositoryAdapter;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateRunningWorkoutCommand;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import({
        MemberRepositoryAdapter.class,
        RunningWorkoutRepositoryAdapter.class
})
class RunningWorkoutRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private RunningWorkoutRepository runningWorkoutRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("회원과 연결된 러닝 운동 기록을 저장한다")
    void saveRunningWorkout() {
        Member member = memberRepository.save(MemberFixture.builder().nickname("runner").buildNew());
        RunningWorkout workout = RunningWorkout.create(
                member,
                AthleteTier.AMATEUR,
                commonWorkoutCommand(),
                new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
        );

        Long workoutId = runningWorkoutRepository.save(member, workout, AthleteTier.AMATEUR);

        Map<String, Object> row = jdbcTemplate.queryForMap("""
                        SELECT member_id, tier, started_at, ended_at, distance, avg_pace, steps
                        FROM running_workout
                        WHERE id = ?
                        """,
                workoutId
        );

        assertThat(workoutId).isNotNull();
        assertThat(row.get("member_id")).isEqualTo(member.getId());
        assertThat(row.get("tier")).isEqualTo(AthleteTier.AMATEUR.name());
        assertThat(row.get("started_at").toString()).contains("2026-07-18 07:00");
        assertThat(row.get("ended_at").toString()).contains("2026-07-18 07:45");
        assertThat(((Number) row.get("distance")).doubleValue()).isEqualTo(8.2);
        assertThat(((Number) row.get("avg_pace")).doubleValue()).isEqualTo(5.48);
        assertThat(((Number) row.get("steps")).intValue()).isEqualTo(7600);
    }

    @Test
    @DisplayName("저장되지 않은 회원으로 러닝 운동 기록을 저장하면 실패한다")
    void saveRunningWorkoutWithUnsavedMemberFails() {
        Member unsavedMember = MemberFixture.builder().nickname("runner").buildNew();
        assertThatThrownBy(() -> {
                    RunningWorkout workout = RunningWorkout.create(
                            unsavedMember,
                            AthleteTier.AMATEUR,
                            commonWorkoutCommand(),
                            new CreateRunningWorkoutCommand(5.48, 4.92, 7600)
                    );
                    runningWorkoutRepository.save(unsavedMember, workout, AthleteTier.AMATEUR);
                })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원 ID");
    }

    private CreateCommonWorkoutCommand commonWorkoutCommand() {
        return new CreateCommonWorkoutCommand(
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
        );
    }
}
