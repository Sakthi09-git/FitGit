package com.fitlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlog.dto.CreateMealRequest;
import com.fitlog.dto.CreateUserRequest;
import com.fitlog.dto.CreateWorkoutRequest;
import com.fitlog.enums.MealType;
import com.fitlog.enums.WorkoutType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FitLogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("REST: Create user and retrieve by ID")
    void userEndpoints_Success() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Integration Tester", "integ@example.com", "9998887776", "pass");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Tester"))
                .andExpect(jsonPath("$.email").value("integ@example.com"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("REST: Reject negative workout calories with 400 Bad Request")
    void workoutEndpoint_NegativeCalories_Rejected() throws Exception {
        CreateWorkoutRequest badRequest = new CreateWorkoutRequest(
                1L,
                WorkoutType.RUNNING,
                30,
                -100, // Negative calories burned!
                LocalDate.of(2026, 9, 28),
                "Should fail"
        );

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("REST: Reject negative meal calories with 400 Bad Request")
    void mealEndpoint_NegativeCalories_Rejected() throws Exception {
        CreateMealRequest badRequest = new CreateMealRequest(
                1L,
                MealType.BREAKFAST,
                "Bad Meal",
                "1 cup",
                -500, // Negative calories!
                LocalDate.of(2026, 9, 28),
                "Should fail"
        );

        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("REST: Retrieve daily calorie summary for initialized sample data")
    void dailySummaryEndpoint_Success() throws Exception {
        // Arun (User 1) on 2026-09-28 has 1800 consumed, 600 burned, 1200 net
        mockMvc.perform(get("/api/users/1/summary/daily?date=2026-09-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.caloriesConsumed").value(1800))
                .andExpect(jsonPath("$.caloriesBurned").value(600))
                .andExpect(jsonPath("$.netCalories").value(1200));
    }

    @Test
    @DisplayName("REST: MANDATORY RULE 2 - Reject weekly summary for week with no entries (404 Not Found)")
    void weeklySummaryEndpoint_NoEntries_Returns404() throws Exception {
        mockMvc.perform(get("/api/users/1/summary/weekly?weekStart=2035-01-01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No workout or meal entries found for the selected week."));
    }

    @Test
    @DisplayName("REST: Retrieve weekly goal progress")
    void goalProgressEndpoint_Success() throws Exception {
        mockMvc.perform(get("/api/users/1/goals/progress?weekStart=2026-09-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.target").value(5))
                .andExpect(jsonPath("$.completed").value(4))
                .andExpect(jsonPath("$.remaining").value(1))
                .andExpect(jsonPath("$.goalAchieved").value(false));
    }

    @Test
    @DisplayName("REST: Reject non-existent user with 404")
    void nonExistentUser_Returns404() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
