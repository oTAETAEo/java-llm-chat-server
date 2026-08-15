package com.example.aisocket.project.application.out;

import com.example.aisocket.project.DataJpaTestSupport;
import com.example.aisocket.project.adapter.out.persistence.MemberRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.RunningWorkoutRepositoryAdapter;
import com.example.aisocket.project.config.QuerydslConfig;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
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
        RunningWorkoutRepositoryAdapter.class,
        QuerydslConfig.class
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
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();

        Long workoutId = runningWorkoutRepository.save(member, workout, AthleteTier.AMATEUR);

        Map<String, Object> row = jdbcTemplate.queryForMap("""
                        SELECT member_id, tier, title, input_source, feedback_count, started_at, ended_at, distance, avg_pace, steps
                        FROM running_workout
                        WHERE id = ?
                        """,
                workoutId
        );
        RunningWorkout savedWorkout = runningWorkoutRepository.findByIdAndMemberId(workoutId, member.getId())
                .orElseThrow();

        assertThat(workoutId).isNotNull();
        assertThat(row.get("member_id")).isEqualTo(member.getId());
        assertThat(row.get("tier")).isEqualTo(AthleteTier.AMATEUR.name());
        assertThat(row.get("title")).isEqualTo("러닝 8.2km");
        assertThat(row.get("input_source")).isEqualTo("DIRECT_INPUT");
        assertThat(((Number) row.get("feedback_count")).longValue()).isZero();
        assertThat(row.get("started_at").toString()).contains("2026-07-18 07:00");
        assertThat(row.get("ended_at").toString()).contains("2026-07-18 07:45");
        assertThat(((Number) row.get("distance")).doubleValue()).isEqualTo(8.2);
        assertThat(row.get("avg_pace")).isInstanceOf(String.class);
        assertThat(row.get("avg_pace").toString()).contains("\"alg\":\"AES-256-GCM\"");
        assertThat(row.get("avg_pace").toString()).doesNotContain("5.48");
        assertThat(row.get("steps")).isInstanceOf(String.class);
        assertThat(row.get("steps").toString()).contains("\"alg\":\"AES-256-GCM\"");
        assertThat(row.get("steps").toString()).doesNotContain("7600");
        assertThat(savedWorkout.getAvgPace()).isEqualTo(5.48);
        assertThat(savedWorkout.getSteps()).isEqualTo(7600);
    }

    @Test
    @DisplayName("저장되지 않은 회원으로 러닝 운동 기록을 저장하면 실패한다")
    void saveRunningWorkoutWithUnsavedMemberFails() {
        Member unsavedMember = MemberFixture.builder().nickname("runner").buildNew();
        assertThatThrownBy(() -> {
                    RunningWorkout workout = RunningWorkoutFixture.builder()
                            .member(unsavedMember)
                            .tier(AthleteTier.AMATEUR)
                            .build();
                    runningWorkoutRepository.save(unsavedMember, workout, AthleteTier.AMATEUR);
                })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원, 시작/종료 시간, 거리, 이동 시간이 같은 러닝 운동을 중복으로 조회한다")
    void findDuplicateRunningWorkout() {
        Member member = memberRepository.save(MemberFixture.builder().nickname("runner").buildNew());
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();
        Long workoutId = runningWorkoutRepository.save(member, workout, AthleteTier.AMATEUR);

        Optional<RunningWorkout> duplicate = runningWorkoutRepository.findDuplicate(member.getId(), workout);

        assertThat(duplicate).isPresent();
        assertThat(duplicate.get().getId()).isEqualTo(workoutId);
    }

}
