package com.fitlog.dto;

import java.time.LocalDate;

public class DailyCalorieBreakdownDto {

    private LocalDate date;
    private Integer caloriesConsumed;
    private Integer caloriesBurned;
    private Integer netCalories;
    private Integer workoutCount;
    private Integer mealCount;

    public DailyCalorieBreakdownDto() {
    }

    public DailyCalorieBreakdownDto(LocalDate date, Integer caloriesConsumed, Integer caloriesBurned,
                                   Integer netCalories, Integer workoutCount, Integer mealCount) {
        this.date = date;
        this.caloriesConsumed = caloriesConsumed;
        this.caloriesBurned = caloriesBurned;
        this.netCalories = netCalories;
        this.workoutCount = workoutCount;
        this.mealCount = mealCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getCaloriesConsumed() {
        return caloriesConsumed;
    }

    public void setCaloriesConsumed(Integer caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Integer getNetCalories() {
        return netCalories;
    }

    public void setNetCalories(Integer netCalories) {
        this.netCalories = netCalories;
    }

    public Integer getWorkoutCount() {
        return workoutCount;
    }

    public void setWorkoutCount(Integer workoutCount) {
        this.workoutCount = workoutCount;
    }

    public Integer getMealCount() {
        return mealCount;
    }

    public void setMealCount(Integer mealCount) {
        this.mealCount = mealCount;
    }
}
