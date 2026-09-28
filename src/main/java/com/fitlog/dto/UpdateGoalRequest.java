package com.fitlog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class UpdateGoalRequest {

    @NotNull(message = "Weekly workout target is required")
    @Positive(message = "Weekly workout target must be greater than zero")
    private Integer weeklyWorkoutTarget;

    @NotNull(message = "Week start date is required")
    private LocalDate weekStartDate;

    public UpdateGoalRequest() {
    }

    public UpdateGoalRequest(Integer weeklyWorkoutTarget, LocalDate weekStartDate) {
        this.weeklyWorkoutTarget = weeklyWorkoutTarget;
        this.weekStartDate = weekStartDate;
    }

    public Integer getWeeklyWorkoutTarget() {
        return weeklyWorkoutTarget;
    }

    public void setWeeklyWorkoutTarget(Integer weeklyWorkoutTarget) {
        this.weeklyWorkoutTarget = weeklyWorkoutTarget;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
}
