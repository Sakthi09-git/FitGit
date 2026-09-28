package com.fitlog.service;

import com.fitlog.dto.DailySummaryResponse;
import com.fitlog.dto.WeeklySummaryResponse;
import com.fitlog.dto.WeeklyWorkoutTrendResponse;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.enums.MealType;
import com.fitlog.enums.WorkoutType;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SummaryService summaryService;

    private User sampleUser;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Arun Kumar", "arun@example.com", "9876543210", "pass");
        sampleUser.setId(1L);
        testDate = LocalDate.of(2026, 9, 28);
    }

    @Test
    @DisplayName("Feature 3: Should calculate daily calories consumed, burned, and net")
    void getDailyCalorieSummary_Success() {
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);

        List<Meal> meals = List.of(
                new Meal(sampleUser, MealType.BREAKFAST, "Oatmeal", "1 bowl", 300, testDate, "notes"),
                new Meal(sampleUser, MealType.LUNCH, "Rice & Veg", "1 plate", 600, testDate, "notes"),
                new Meal(sampleUser, MealType.SNACK, "Fruit", "1 bowl", 200, testDate, "notes"),
                new Meal(sampleUser, MealType.DINNER, "Chicken & Rice", "1 plate", 700, testDate, "notes")
        ); // Total 1800 cal consumed

        List<Workout> workouts = List.of(
                new Workout(sampleUser, WorkoutType.RUNNING, 30, 250, testDate, "run"),
                new Workout(sampleUser, WorkoutType.WALKING, 30, 120, testDate, "walk"),
                new Workout(sampleUser, WorkoutType.GYM, 35, 230, testDate, "gym")
        ); // Total 600 cal burned

        when(mealRepository.findByUserIdAndMealDate(1L, testDate)).thenReturn(meals);
        when(workoutRepository.findByUserIdAndWorkoutDate(1L, testDate)).thenReturn(workouts);

        DailySummaryResponse response = summaryService.getDailyCalorieSummary(1L, testDate);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(testDate, response.getDate());
        assertEquals(1800, response.getCaloriesConsumed());
        assertEquals(600, response.getCaloriesBurned());
        assertEquals(1200, response.getNetCalories()); // 1800 - 600 = 1200
    }

    @Test
    @DisplayName("MANDATORY RULE 2: Weekly summary requires data - reject with 404 when no entries exist")
    void getWeeklySummary_NoDataInWeek_ThrowsResourceNotFoundException() {
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);

        LocalDate emptyWeekStart = LocalDate.of(2030, 1, 1);
        LocalDate emptyWeekEnd = emptyWeekStart.plusDays(6);

        when(workoutRepository.findByUserIdAndWorkoutDateBetween(1L, emptyWeekStart, emptyWeekEnd))
                .thenReturn(Collections.emptyList());
        when(mealRepository.findByUserIdAndMealDateBetween(1L, emptyWeekStart, emptyWeekEnd))
                .thenReturn(Collections.emptyList());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                summaryService.getWeeklySummary(1L, emptyWeekStart));

        assertEquals("No workout or meal entries found for the selected week.", ex.getMessage());
    }

    @Test
    @DisplayName("Should generate weekly summary when data exists")
    void getWeeklySummary_WithData_Success() {
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);

        LocalDate weekEnd = testDate.plusDays(6);
        List<Workout> workouts = List.of(
                new Workout(sampleUser, WorkoutType.RUNNING, 30, 300, testDate, "run")
        );
        List<Meal> meals = List.of(
                new Meal(sampleUser, MealType.LUNCH, "Rice", "1 plate", 500, testDate, "lunch")
        );

        when(workoutRepository.findByUserIdAndWorkoutDateBetween(1L, testDate, weekEnd)).thenReturn(workouts);
        when(mealRepository.findByUserIdAndMealDateBetween(1L, testDate, weekEnd)).thenReturn(meals);

        WeeklySummaryResponse response = summaryService.getWeeklySummary(1L, testDate);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(testDate, response.getWeekStart());
        assertEquals(weekEnd, response.getWeekEnd());
        assertEquals(500, response.getTotalCaloriesConsumed());
        assertEquals(300, response.getTotalCaloriesBurned());
        assertEquals(200, response.getNetCalories());
        assertEquals(1, response.getTotalWorkouts());
        assertEquals(1, response.getTotalMeals());
        assertEquals(7, response.getDailyBreakdown().size());
    }

    @Test
    @DisplayName("Feature 4: Should calculate weekly workout trend")
    void getWeeklyWorkoutTrend_Success() {
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);

        LocalDate weekEnd = testDate.plusDays(6);
        List<Workout> workouts = List.of(
                new Workout(sampleUser, WorkoutType.RUNNING, 30, 250, testDate, "run"),
                new Workout(sampleUser, WorkoutType.WALKING, 30, 150, testDate, "walk"),
                new Workout(sampleUser, WorkoutType.CYCLING, 60, 500, testDate.plusDays(2), "cycling")
        );

        when(workoutRepository.findByUserIdAndWorkoutDateBetween(1L, testDate, weekEnd)).thenReturn(workouts);

        WeeklyWorkoutTrendResponse response = summaryService.getWeeklyWorkoutTrend(1L, testDate);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(3, response.getTotalWorkouts());
        assertEquals(120, response.getTotalDurationMinutes());
        assertEquals(900, response.getTotalCaloriesBurned());
        assertEquals(7, response.getDailyTrend().size());
    }
}
