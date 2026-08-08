package com.example.aisocket.project.application.out;

import com.example.aisocket.project.DataJpaTestSupport;
import com.example.aisocket.project.adapter.out.persistence.CyclingWorkoutRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.MemberRepositoryAdapter;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.Optional;

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
        Member member = memberRepository.save(MemberFixture.builder().nickname("rider").buildNew());
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.PRO)
                .build();

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
        Member unsavedMember = MemberFixture.builder().nickname("rider").buildNew();
        assertThatThrownBy(() -> {
                    CyclingWorkout workout = CyclingWorkoutFixture.builder()
                            .member(unsavedMember)
                            .tier(AthleteTier.PRO)
                            .build();
                    cyclingWorkoutRepository.save(unsavedMember, workout, AthleteTier.PRO);
                })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원, 시작/종료 시간, 거리, 이동 시간이 같은 자전거 운동을 중복으로 조회한다")
    void findDuplicateCyclingWorkout() {
        Member member = memberRepository.save(MemberFixture.builder().nickname("rider").buildNew());
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.PRO)
                .build();
        Long workoutId = cyclingWorkoutRepository.save(member, workout, AthleteTier.PRO);

        Optional<CyclingWorkout> duplicate = cyclingWorkoutRepository.findDuplicate(member.getId(), workout);

        assertThat(duplicate).isPresent();
        assertThat(duplicate.get().getId()).isEqualTo(workoutId);
    }

}
