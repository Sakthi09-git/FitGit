package com.fitlog.dto;

import java.time.LocalDate;

public class GoalProgressResponse {

    private Long userId;
    private LocalDate weekStart;
    private Integer target;
    private Integer completed;
    private Integer remaining;
    private Boolean goalAchieved;

    public GoalProgressResponse() {
    }

    public GoalProgressResponse(Long userId, LocalDate weekStart, Integer target, Integer completed, Integer remaining, Boolean goalAchieved) {
        this.userId = userId;
        this.weekStart = weekStart;
        this.target = target;
        this.completed = completed;
        this.remaining = remaining;
        this.goalAchieved = goalAchieved;
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

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public Boolean getGoalAchieved() {
        return goalAchieved;
    }

    public void setGoalAchieved(Boolean goalAchieved) {
        this.goalAchieved = goalAchieved;
    }
}
