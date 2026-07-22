package com.example.aisocket.project.adapter.in;

import com.example.aisocket.project.adapter.in.factory.CyclingWorkoutCreateStrategy;
import com.example.aisocket.project.adapter.in.factory.RunningWorkoutCreateStrategy;
import com.example.aisocket.project.adapter.in.factory.WorkoutFactory;
import com.example.aisocket.project.application.in.CoachFeedback;
import com.example.aisocket.project.config.SecurityConfig;
import com.example.aisocket.project.domain.AthleteTier;
import com.example.aisocket.project.domain.CyclingWorkout;
import com.example.aisocket.project.domain.Member;
import com.example.aisocket.project.domain.RunningWorkout;
import com.example.aisocket.project.domain.Workout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoachFeedbackController.class)
@Import({
        WorkoutFactory.class,
        RunningWorkoutCreateStrategy.class,
        CyclingWorkoutCreateStrategy.class,
        SecurityConfig.class
})
class CoachFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoachFeedback coachFeedback;

    @Test
    @DisplayName("러닝 단일 운동 피드백 SSE 요청을 처리한다")
    void generateRunningWorkoutFeedbackStream() throws Exception {

        doAnswer(invocation -> {
            Consumer<String> chunkConsumer = invocation.getArgument(3);
            chunkConsumer.accept("running feedback");
            return null;
        }).when(coachFeedback).getFeedbackStream(any(Member.class), any(Workout.class), eq(AthleteTier.AMATEUR), any());

        MvcResult result = mockMvc.perform(post("/api/v1/coach/feedback/single/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(runningRequestJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        result.getAsyncResult(1_000);

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("running feedback")));

        ArgumentCaptor<Workout> workoutCaptor = ArgumentCaptor.forClass(Workout.class);
        verify(coachFeedback).getFeedbackStream(any(Member.class), workoutCaptor.capture(), eq(AthleteTier.AMATEUR), any());

        assertThat(workoutCaptor.getValue()).isInstanceOf(RunningWorkout.class);
        assertThat(workoutCaptor.getValue().getDistance()).isEqualTo(8.2);
    }

    @Test
    @DisplayName("자전거 단일 운동 피드백 SSE 요청을 처리한다")
    void generateCyclingWorkoutFeedbackStream() throws Exception {
        doAnswer(invocation -> {
            Consumer<String> chunkConsumer = invocation.getArgument(3);
            chunkConsumer.accept("cycling feedback");
            return null;
        }).when(coachFeedback).getFeedbackStream(any(Member.class), any(Workout.class), eq(AthleteTier.PRO), any());

        MvcResult result = mockMvc.perform(post("/api/v1/coach/feedback/single/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(cyclingRequestJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        result.getAsyncResult(1_000);

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("cycling feedback")));

        ArgumentCaptor<Workout> workoutCaptor = ArgumentCaptor.forClass(Workout.class);
        verify(coachFeedback).getFeedbackStream(any(Member.class), workoutCaptor.capture(), eq(AthleteTier.PRO), any());

        assertThat(workoutCaptor.getValue()).isInstanceOf(CyclingWorkout.class);
        assertThat(workoutCaptor.getValue().getDistance()).isEqualTo(42.5);
    }

    private String runningRequestJson() {
        return """
                {
                  "workOutType": "RUNNING",
                  "tier": "AMATEUR",
                  "startedAt": "2026-07-18T07:00:00",
                  "endedAt": "2026-07-18T07:45:00",
                  "distance": 8.2,
                  "elevGain": 120.0,
                  "elevationMax": 85.0,
                  "movingTime": 45,
                  "calories": 530.0,
                  "avgCadence": 172.0,
                  "maxCadence": 188.0,
                  "maxHeartRate": 176.0,
                  "avgHeartRate": 148.0,
                  "avgPace": 5.48,
                  "maxPace": 4.92,
                  "steps": 7600
                }
                """;
    }

    private String cyclingRequestJson() {
        return """
                {
                  "workOutType": "CYCLING",
                  "tier": "PRO",
                  "startedAt": "2026-07-18T09:00:00",
                  "endedAt": "2026-07-18T10:30:00",
                  "distance": 42.5,
                  "elevGain": 650.0,
                  "elevationMax": 240.0,
                  "movingTime": 90,
                  "calories": 920.0,
                  "avgCadence": 88.0,
                  "maxCadence": 104.0,
                  "maxHeartRate": 168.0,
                  "avgHeartRate": 142.0,
                  "avgSpeed": 27.4,
                  "maxSpeed": 44.1,
                  "avgPower": 185.0,
                  "maxPower": 420.0,
                  "ftp": 250.0
                }
                """;
    }
}
