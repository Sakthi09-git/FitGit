package com.fitlog.dto;

import java.time.LocalDate;

public class DailySummaryResponse {

    private Long userId;
    private LocalDate date;
    private Integer caloriesConsumed;
    private Integer caloriesBurned;
    private Integer netCalories;

    public DailySummaryResponse() {
    }

    public DailySummaryResponse(Long userId, LocalDate date, Integer caloriesConsumed, Integer caloriesBurned, Integer netCalories) {
        this.userId = userId;
        this.date = date;
        this.caloriesConsumed = caloriesConsumed;
        this.caloriesBurned = caloriesBurned;
        this.netCalories = netCalories;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
