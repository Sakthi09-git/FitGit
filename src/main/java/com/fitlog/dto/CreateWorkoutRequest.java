package com.fitlog.dto;

import com.fitlog.enums.WorkoutType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class CreateWorkoutRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Workout type is required")
    private WorkoutType workoutType;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than zero")
    private Integer durationMinutes;

    @NotNull(message = "Calories burned is required")
    @PositiveOrZero(message = "Calories burned must be non-negative")
    private Integer caloriesBurned;

    @NotNull(message = "Workout date is required")
    private LocalDate workoutDate;

    private String notes;

    public CreateWorkoutRequest() {
    }

    public CreateWorkoutRequest(Long userId, WorkoutType workoutType, Integer durationMinutes, Integer caloriesBurned, LocalDate workoutDate, String notes) {
        this.userId = userId;
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.workoutDate = workoutDate;
        this.notes = notes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
