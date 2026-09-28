package com.fitlog.dto;

import java.time.LocalDate;

public class DailyWorkoutTrendDto {

    private LocalDate date;
    private Integer workoutCount;
    private Integer totalDurationMinutes;
    private Integer caloriesBurned;

    public DailyWorkoutTrendDto() {
    }

    public DailyWorkoutTrendDto(LocalDate date, Integer workoutCount, Integer totalDurationMinutes, Integer caloriesBurned) {
        this.date = date;
        this.workoutCount = workoutCount;
        this.totalDurationMinutes = totalDurationMinutes;
        this.caloriesBurned = caloriesBurned;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getWorkoutCount() {
        return workoutCount;
    }

    public void setWorkoutCount(Integer workoutCount) {
        this.workoutCount = workoutCount;
    }

    public Integer getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(Integer totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
}
