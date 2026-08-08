package com.example.aisocket.project.application.internal.workout;

import com.example.aisocket.project.SpringBootIntegrationTestSupport;
import com.example.aisocket.project.application.out.CyclingWorkoutSensorDataRepository;
import com.example.aisocket.project.application.out.RunningWorkoutSensorDataRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class WorkoutSensorDataRegisterServiceTest extends SpringBootIntegrationTestSupport {

    @Autowired
    private WorkoutSensorDataRegisterService workoutSensorDataRegisterService;

    @MockitoBean
    private RunningWorkoutSensorDataRepository runningWorkoutSensorDataRepository;

    @MockitoBean
    private CyclingWorkoutSensorDataRepository cyclingWorkoutSensorDataRepository;

    @Test
    @DisplayName("러닝 운동이면 러닝 센서 데이터 저장소에 저장한다")
    void registerRunningSensorData() {
        Member member = MemberFixture.builder().id(1L).build();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.AMATEUR)
                .build();
        CreateWorkoutSensorDataCommand command = new CreateWorkoutSensorDataCommand("[{\"heartRate\":150}]");

        workoutSensorDataRegisterService.register(new WorkoutRecordRegistration(10L, member, workout), command);

        verify(runningWorkoutSensorDataRepository).save(workout, command);
        verifyNoInteractions(cyclingWorkoutSensorDataRepository);
    }

    @Test
    @DisplayName("자전거 운동이면 자전거 센서 데이터 저장소에 저장한다")
    void registerCyclingSensorData() {
        Member member = MemberFixture.builder().id(1L).build();
        CyclingWorkout workout = CyclingWorkoutFixture.builder()
                .member(member)
                .tier(AthleteTier.PRO)
                .build();
        CreateWorkoutSensorDataCommand command = new CreateWorkoutSensorDataCommand("[{\"power\":220}]");

        workoutSensorDataRegisterService.register(new WorkoutRecordRegistration(10L, member, workout), command);

        verify(cyclingWorkoutSensorDataRepository).save(workout, command);
        verifyNoInteractions(runningWorkoutSensorDataRepository);
    }

    @Test
    @DisplayName("센서 데이터가 없으면 저장소를 호출하지 않는다")
    void registerWithoutSensorData() {
        Member member = MemberFixture.builder().id(1L).build();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .build();

        workoutSensorDataRegisterService.register(new WorkoutRecordRegistration(10L, member, workout), null);

        verifyNoInteractions(runningWorkoutSensorDataRepository, cyclingWorkoutSensorDataRepository);
    }

    @Test
    @DisplayName("중복 운동이면 센서 데이터를 다시 저장하지 않는다")
    void registerDuplicateWorkoutSensorData() {
        Member member = MemberFixture.builder().id(1L).build();
        RunningWorkout workout = RunningWorkoutFixture.builder()
                .member(member)
                .build();
        CreateWorkoutSensorDataCommand command = new CreateWorkoutSensorDataCommand("[{\"heartRate\":150}]");

        workoutSensorDataRegisterService.register(new WorkoutRecordRegistration(10L, member, workout, false), command);

        verifyNoInteractions(runningWorkoutSensorDataRepository, cyclingWorkoutSensorDataRepository);
    }
}
