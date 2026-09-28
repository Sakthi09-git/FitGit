package com.fitlog.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeeklyWorkoutTrendResponse {

    private Long userId;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Integer totalWorkouts;
    private Integer totalDurationMinutes;
    private Integer totalCaloriesBurned;
    private List<DailyWorkoutTrendDto> dailyTrend = new ArrayList<>();

    public WeeklyWorkoutTrendResponse() {
    }

    public WeeklyWorkoutTrendResponse(Long userId, LocalDate weekStart, LocalDate weekEnd,
                                      Integer totalWorkouts, Integer totalDurationMinutes,
                                      Integer totalCaloriesBurned, List<DailyWorkoutTrendDto> dailyTrend) {
        this.userId = userId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.totalWorkouts = totalWorkouts;
        this.totalDurationMinutes = totalDurationMinutes;
        this.totalCaloriesBurned = totalCaloriesBurned;
        this.dailyTrend = dailyTrend;
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

    public Integer getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(Integer totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public Integer getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(Integer totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public Integer getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(Integer totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public List<DailyWorkoutTrendDto> getDailyTrend() {
        return dailyTrend;
    }

    public void setDailyTrend(List<DailyWorkoutTrendDto> dailyTrend) {
        this.dailyTrend = dailyTrend;
    }
}
