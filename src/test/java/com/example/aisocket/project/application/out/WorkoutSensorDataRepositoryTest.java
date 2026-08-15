package com.example.aisocket.project.application.out;

import com.example.aisocket.project.DataJpaTestSupport;
import com.example.aisocket.project.adapter.out.persistence.CyclingWorkoutRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.CyclingWorkoutSensorDataRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.MemberRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.RunningWorkoutRepositoryAdapter;
import com.example.aisocket.project.adapter.out.persistence.RunningWorkoutSensorDataRepositoryAdapter;
import com.example.aisocket.project.config.QuerydslConfig;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CreateWorkoutSensorDataCommand;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.CyclingWorkoutFixture;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.MemberFixture;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.RunningWorkoutFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Import({
        MemberRepositoryAdapter.class,
        RunningWorkoutRepositoryAdapter.class,
        CyclingWorkoutRepositoryAdapter.class,
        RunningWorkoutSensorDataRepositoryAdapter.class,
        CyclingWorkoutSensorDataRepositoryAdapter.class,
        QuerydslConfig.class
})
class WorkoutSensorDataRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RunningWorkoutRepository runningWorkoutRepository;

    @Autowired
    private CyclingWorkoutRepository cyclingWorkoutRepository;

    @Autowired
    private RunningWorkoutSensorDataRepository runningWorkoutSensorDataRepository;

    @Autowired
    private CyclingWorkoutSensorDataRepository cyclingWorkoutSensorDataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("러닝 센서 샘플 JSON은 암호화해서 저장하고 조회 시 복호화한다")
    void saveEncryptedRunningSensorSamples() {
        Member member = memberRepository.save(MemberFixture.builder().nickname("runner").buildNew());
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();
        Long workoutId = runningWorkoutRepository.save(member, workout, AthleteTier.AMATEUR);
        String samplesJson = "[{\"heartRate\":150,\"latitude\":37.1}]";

        runningWorkoutSensorDataRepository.save(workout, new CreateWorkoutSensorDataCommand(samplesJson));

        String stored = jdbcTemplate.queryForObject("""
                        SELECT samples_encrypted
                        FROM running_workout_sensor_data
                        WHERE running_workout_id = ?
                        """,
                String.class,
                workoutId
        );

        assertThat(stored).contains("\"alg\":\"AES-256-GCM\"");
        assertThat(stored).doesNotContain("heartRate");
        assertThat(stored).doesNotContain("37.1");
        assertThat(runningWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workoutId))
                .contains(samplesJson);
    }

    @Test
    @DisplayName("자전거 센서 샘플 JSON은 암호화해서 저장하고 조회 시 복호화한다")
    void saveEncryptedCyclingSensorSamples() {
        Member member = memberRepository.save(MemberFixture.builder().nickname("rider").buildNew());
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.PRO)
                .build();
        Long workoutId = cyclingWorkoutRepository.save(member, workout, AthleteTier.PRO);
        String samplesJson = "[{\"power\":220,\"longitude\":127.1}]";

        cyclingWorkoutSensorDataRepository.save(workout, new CreateWorkoutSensorDataCommand(samplesJson));

        String stored = jdbcTemplate.queryForObject("""
                        SELECT samples_encrypted
                        FROM cycling_workout_sensor_data
                        WHERE cycling_workout_id = ?
                        """,
                String.class,
                workoutId
        );

        assertThat(stored).contains("\"alg\":\"AES-256-GCM\"");
        assertThat(stored).doesNotContain("power");
        assertThat(stored).doesNotContain("127.1");
        assertThat(cyclingWorkoutSensorDataRepository.findSamplesJsonByWorkoutId(workoutId))
                .contains(samplesJson);
    }
}
