package com.fitlog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Goal Entity representing a user's weekly workout-count target.
 */
@Entity
@Table(name = "goals", indexes = {
        @Index(name = "idx_goal_user_id", columnList = "user_id"),
        @Index(name = "idx_goal_week_start", columnList = "week_start_date"),
        @Index(name = "idx_goal_user_week", columnList = "user_id, week_start_date")
})
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Weekly workout target is required")
    @Positive(message = "Weekly workout target must be greater than zero")
    @Column(name = "weekly_workout_target", nullable = false)
    private Integer weeklyWorkoutTarget;

    @NotNull(message = "Week start date is required")
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Goal() {
    }

    public Goal(User user, Integer weeklyWorkoutTarget, LocalDate weekStartDate) {
        this.user = user;
        this.weeklyWorkoutTarget = weeklyWorkoutTarget;
        this.weekStartDate = weekStartDate;
    }

    public Goal(Long id, User user, Integer weeklyWorkoutTarget, LocalDate weekStartDate) {
        this.id = id;
        this.user = user;
        this.weeklyWorkoutTarget = weeklyWorkoutTarget;
        this.weekStartDate = weekStartDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", weeklyWorkoutTarget=" + weeklyWorkoutTarget +
                ", weekStartDate=" + weekStartDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
