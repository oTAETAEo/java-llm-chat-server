package com.example.aisocket.project.domain;

import jakarta.persistence.Column;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "cycling_workout_sensor_data")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CyclingWorkoutSensorData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycling_workout_id", nullable = false, unique = true)
    private CyclingWorkout workout;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String samplesJson;

    public static CyclingWorkoutSensorData create(CyclingWorkout workout, CreateWorkoutSensorDataCommand command) {
        return new CyclingWorkoutSensorData(workout, command);
    }

    private CyclingWorkoutSensorData(CyclingWorkout workout, CreateWorkoutSensorDataCommand command) {
        this.workout = workout;
        this.samplesJson = command.samplesJson();

        validate();
    }

    private void validate() {
        if (workout == null || workout.getId() == null) {
            throw new IllegalArgumentException("자전거 운동 기록(workout)은 센서 데이터에 필수 값입니다.");
        }
        if (samplesJson == null || samplesJson.isBlank()) {
            throw new IllegalArgumentException("센서 JSON(samplesJson)은 필수 값입니다.");
        }
    }
}
