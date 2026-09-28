package com.fitlog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitlog.entity.Goal;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GoalResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Integer weeklyWorkoutTarget;
    private LocalDate weekStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public GoalResponse() {
    }

    public GoalResponse(Long id, Long userId, String userName, Integer weeklyWorkoutTarget,
                        LocalDate weekStartDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.weeklyWorkoutTarget = weeklyWorkoutTarget;
        this.weekStartDate = weekStartDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GoalResponse fromEntity(Goal goal) {
        if (goal == null) {
            return null;
        }
        return new GoalResponse(
                goal.getId(),
                goal.getUser() != null ? goal.getUser().getId() : null,
                goal.getUser() != null ? goal.getUser().getName() : null,
                goal.getWeeklyWorkoutTarget(),
                goal.getWeekStartDate(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
