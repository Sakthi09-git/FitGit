package com.fitlog.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeeklySummaryResponse {

    private Long userId;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Integer totalCaloriesConsumed;
    private Integer totalCaloriesBurned;
    private Integer netCalories;
    private Integer totalWorkouts;
    private Integer totalMeals;
    private List<DailyCalorieBreakdownDto> dailyBreakdown = new ArrayList<>();

    public WeeklySummaryResponse() {
    }

    public WeeklySummaryResponse(Long userId, LocalDate weekStart, LocalDate weekEnd,
                                 Integer totalCaloriesConsumed, Integer totalCaloriesBurned,
                                 Integer netCalories, Integer totalWorkouts, Integer totalMeals,
                                 List<DailyCalorieBreakdownDto> dailyBreakdown) {
        this.userId = userId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.totalCaloriesConsumed = totalCaloriesConsumed;
        this.totalCaloriesBurned = totalCaloriesBurned;
        this.netCalories = netCalories;
        this.totalWorkouts = totalWorkouts;
        this.totalMeals = totalMeals;
        this.dailyBreakdown = dailyBreakdown;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(LocalDate weekEnd) {
        this.weekEnd = weekEnd;
    }

    public Integer getTotalCaloriesConsumed() {
        return totalCaloriesConsumed;
    }

    public void setTotalCaloriesConsumed(Integer totalCaloriesConsumed) {
        this.totalCaloriesConsumed = totalCaloriesConsumed;
    }

    public Integer getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(Integer totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public Integer getNetCalories() {
        return netCalories;
    }

    public void setNetCalories(Integer netCalories) {
        this.netCalories = netCalories;
    }

    public Integer getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(Integer totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public Integer getTotalMeals() {
        return totalMeals;
    }

    public void setTotalMeals(Integer totalMeals) {
        this.totalMeals = totalMeals;
    }

    public List<DailyCalorieBreakdownDto> getDailyBreakdown() {
        return dailyBreakdown;
    }

    public void setDailyBreakdown(List<DailyCalorieBreakdownDto> dailyBreakdown) {
        this.dailyBreakdown = dailyBreakdown;
    }
}
