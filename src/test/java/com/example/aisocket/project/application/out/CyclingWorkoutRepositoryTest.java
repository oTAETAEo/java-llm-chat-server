package com.example.aisocket.project.application.out;

import com.example.aisocket.project.DataJpaTestSupport;
import com.example.aisocket.project.adapter.out.persistence.CyclingWorkoutRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.MemberRepositoryAdapter;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateCommonWorkoutCommand;
import com.example.aisocket.project.domain.CreateCyclingWorkoutCommand;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
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
        CyclingWorkoutRepositoryAdapter.class
})
class CyclingWorkoutRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private CyclingWorkoutRepository cyclingWorkoutRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("회원과 연결된 자전거 운동 기록을 저장한다")
    void saveCyclingWorkout() {
        Member member = memberRepository.save(Member.create(null, null, "rider"));
        CyclingWorkout workout = CyclingWorkout.create(
                member,
                AthleteTier.PRO,
                commonWorkoutCommand(),
                new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
        );

        Long workoutId = cyclingWorkoutRepository.save(member, workout, AthleteTier.PRO);

        Map<String, Object> row = jdbcTemplate.queryForMap("""
                        SELECT member_id, tier, started_at, ended_at, distance, avg_speed, avg_power, ftp
                        FROM cycling_workout
                        WHERE id = ?
                        """,
                workoutId
        );

        assertThat(workoutId).isNotNull();
        assertThat(row.get("member_id")).isEqualTo(member.getId());
        assertThat(row.get("tier")).isEqualTo(AthleteTier.PRO.name());
        assertThat(row.get("started_at").toString()).contains("2026-07-18 09:00");
        assertThat(row.get("ended_at").toString()).contains("2026-07-18 10:30");
        assertThat(((Number) row.get("distance")).doubleValue()).isEqualTo(42.5);
        assertThat(((Number) row.get("avg_speed")).doubleValue()).isEqualTo(27.4);
        assertThat(((Number) row.get("avg_power")).doubleValue()).isEqualTo(185.0);
        assertThat(((Number) row.get("ftp")).doubleValue()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("저장되지 않은 회원으로 자전거 운동 기록을 저장하면 실패한다")
    void saveCyclingWorkoutWithUnsavedMemberFails() {
        Member unsavedMember = Member.create(null, null, "rider");
        assertThatThrownBy(() -> {
                    CyclingWorkout workout = CyclingWorkout.create(
                            unsavedMember,
                            AthleteTier.PRO,
                            commonWorkoutCommand(),
                            new CreateCyclingWorkoutCommand(27.4, 44.1, 185.0, 420.0, 250.0)
                    );
                    cyclingWorkoutRepository.save(unsavedMember, workout, AthleteTier.PRO);
                })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원 ID");
    }

    private CreateCommonWorkoutCommand commonWorkoutCommand() {
        return new CreateCommonWorkoutCommand(
                LocalDateTime.of(2026, 7, 18, 9, 0),
                LocalDateTime.of(2026, 7, 18, 10, 30),
                42.5,
                650.0,
                240.0,
                90,
                920.0,
                88.0,
                104.0,
                168.0,
                142.0
        );
    }
}
