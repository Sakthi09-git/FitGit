package com.fitlog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitlog.entity.Workout;
import com.fitlog.enums.WorkoutType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WorkoutResponse {

    private Long id;
    private Long userId;
    private String userName;
    private WorkoutType workoutType;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private LocalDate workoutDate;
    private String notes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public WorkoutResponse() {
    }

    public WorkoutResponse(Long id, Long userId, String userName, WorkoutType workoutType,
                           Integer durationMinutes, Integer caloriesBurned, LocalDate workoutDate,
                           String notes, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.workoutDate = workoutDate;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static WorkoutResponse fromEntity(Workout workout) {
        if (workout == null) {
            return null;
        }
        return new WorkoutResponse(
                workout.getId(),
                workout.getUser() != null ? workout.getUser().getId() : null,
                workout.getUser() != null ? workout.getUser().getName() : null,
                workout.getWorkoutType(),
                workout.getDurationMinutes(),
                workout.getCaloriesBurned(),
                workout.getWorkoutDate(),
                workout.getNotes(),
                workout.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
