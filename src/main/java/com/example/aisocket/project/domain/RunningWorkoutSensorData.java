package com.example.aisocket.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.example.aisocket.project.domain.encryption.EncryptedStringAttributeConverter;

@Getter
@Entity
@Table(name = "running_workout_sensor_data")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningWorkoutSensorData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_workout_id", nullable = false, unique = true)
    private RunningWorkout workout;

    @Convert(converter = EncryptedStringAttributeConverter.class)
    @Column(name = "samples_encrypted", columnDefinition = "text", nullable = false)
    private String samplesJson;

    public static RunningWorkoutSensorData create(RunningWorkout workout, CreateWorkoutSensorDataCommand command) {
        return new RunningWorkoutSensorData(workout, command);
    }

    private RunningWorkoutSensorData(RunningWorkout workout, CreateWorkoutSensorDataCommand command) {
        this.workout = workout;
        this.samplesJson = command.samplesJson();

        validate();
    }

    private void validate() {
        if (workout == null || workout.getId() == null) {
            throw new IllegalArgumentException("러닝 운동 기록(workout)은 센서 데이터에 필수 값입니다.");
        }
        if (samplesJson == null || samplesJson.isBlank()) {
            throw new IllegalArgumentException("센서 JSON(samplesJson)은 필수 값입니다.");
        }
    }
}
