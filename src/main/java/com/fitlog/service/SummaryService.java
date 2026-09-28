package com.fitlog.service;

import com.fitlog.dto.DailyCalorieBreakdownDto;
import com.fitlog.dto.DailySummaryResponse;
import com.fitlog.dto.DailyWorkoutTrendDto;
import com.fitlog.dto.WeeklySummaryResponse;
import com.fitlog.dto.WeeklyWorkoutTrendResponse;
import com.fitlog.entity.Meal;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service calculating daily and weekly summaries, calorie balances, and workout trends
 * from persisted database records.
 */
@Service
@Transactional(readOnly = true)
public class SummaryService {

    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final UserService userService;

    @Autowired
    public SummaryService(WorkoutRepository workoutRepository, MealRepository mealRepository, UserService userService) {
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.userService = userService;
    }

    /**
     * Feature 3: Daily Calorie Summary.
     * Calculates Calories Consumed (Meals), Calories Burned (Workouts), and Net Calories.
     */
    public DailySummaryResponse getDailyCalorieSummary(Long userId, LocalDate date) {
        userService.getUserEntityById(userId);

        if (date == null) {
            throw new BusinessRuleException("Date parameter is required.");
        }

        List<Meal> meals = mealRepository.findByUserIdAndMealDate(userId, date);
        List<Workout> workouts = workoutRepository.findByUserIdAndWorkoutDate(userId, date);

        int caloriesConsumed = meals.stream()
                .mapToInt(Meal::getCalories)
                .sum();

        int caloriesBurned = workouts.stream()
                .mapToInt(Workout::getCaloriesBurned)
                .sum();

        int netCalories = caloriesConsumed - caloriesBurned;

        return new DailySummaryResponse(userId, date, caloriesConsumed, caloriesBurned, netCalories);
    }

    /**
     * Weekly Calorie & Activity Summary.
     * MANDATORY RULE 2:
     * A weekly summary should only be generated when at least one relevant entry exists for that week.
     * If no workout/meal entries exist, reject with HTTP 404 and a meaningful message.
     */
    public WeeklySummaryResponse getWeeklySummary(Long userId, LocalDate weekStart) {
        userService.getUserEntityById(userId);

        if (weekStart == null) {
            throw new BusinessRuleException("Week start date parameter is required.");
        }

        LocalDate weekEnd = weekStart.plusDays(6);

        List<Workout> workouts = workoutRepository.findByUserIdAndWorkoutDateBetween(userId, weekStart, weekEnd);
        List<Meal> meals = mealRepository.findByUserIdAndMealDateBetween(userId, weekStart, weekEnd);

        // ENFORCE MANDATORY RULE 2: Weekly summary requires at least one relevant entry
        if (workouts.isEmpty() && meals.isEmpty()) {
            throw new ResourceNotFoundException("No workout or meal entries found for the selected week.");
        }

        int totalCaloriesConsumed = meals.stream().mapToInt(Meal::getCalories).sum();
        int totalCaloriesBurned = workouts.stream().mapToInt(Workout::getCaloriesBurned).sum();
        int netCalories = totalCaloriesConsumed - totalCaloriesBurned;

        // Group by date for the 7-day breakdown
        Map<LocalDate, List<Meal>> mealsByDate = meals.stream()
                .collect(Collectors.groupingBy(Meal::getMealDate));
        Map<LocalDate, List<Workout>> workoutsByDate = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getWorkoutDate));

        List<DailyCalorieBreakdownDto> dailyBreakdown = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = weekStart.plusDays(i);
            List<Meal> dayMeals = mealsByDate.getOrDefault(currentDay, List.of());
            List<Workout> dayWorkouts = workoutsByDate.getOrDefault(currentDay, List.of());

            int dayConsumed = dayMeals.stream().mapToInt(Meal::getCalories).sum();
            int dayBurned = dayWorkouts.stream().mapToInt(Workout::getCaloriesBurned).sum();
            int dayNet = dayConsumed - dayBurned;

            dailyBreakdown.add(new DailyCalorieBreakdownDto(
                    currentDay,
                    dayConsumed,
                    dayBurned,
                    dayNet,
                    dayWorkouts.size(),
                    dayMeals.size()
            ));
        }

        return new WeeklySummaryResponse(
                userId,
                weekStart,
                weekEnd,
                totalCaloriesConsumed,
                totalCaloriesBurned,
                netCalories,
                workouts.size(),
                meals.size(),
                dailyBreakdown
        );
    }

    /**
     * Feature 4: Weekly Workout Trend.
     * Aggregates workout count, total duration, and calories burned per day and overall.
     */
    public WeeklyWorkoutTrendResponse getWeeklyWorkoutTrend(Long userId, LocalDate weekStart) {
        userService.getUserEntityById(userId);

        if (weekStart == null) {
            throw new BusinessRuleException("Week start date parameter is required.");
        }

        LocalDate weekEnd = weekStart.plusDays(6);
        List<Workout> workouts = workoutRepository.findByUserIdAndWorkoutDateBetween(userId, weekStart, weekEnd);

        int totalWorkouts = workouts.size();
        int totalDurationMinutes = workouts.stream().mapToInt(Workout::getDurationMinutes).sum();
        int totalCaloriesBurned = workouts.stream().mapToInt(Workout::getCaloriesBurned).sum();

        Map<LocalDate, List<Workout>> workoutsByDate = workouts.stream()
                .collect(Collectors.groupingBy(Workout::getWorkoutDate));

        List<DailyWorkoutTrendDto> dailyTrend = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = weekStart.plusDays(i);
            List<Workout> dayWorkouts = workoutsByDate.getOrDefault(currentDay, List.of());

            int count = dayWorkouts.size();
            int duration = dayWorkouts.stream().mapToInt(Workout::getDurationMinutes).sum();
            int burned = dayWorkouts.stream().mapToInt(Workout::getCaloriesBurned).sum();

            dailyTrend.add(new DailyWorkoutTrendDto(currentDay, count, duration, burned));
        }

        return new WeeklyWorkoutTrendResponse(
                userId,
                weekStart,
                weekEnd,
                totalWorkouts,
                totalDurationMinutes,
                totalCaloriesBurned,
                dailyTrend
        );
    }
}
