package com.fitlog.entity;

import com.fitlog.enums.WorkoutType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Workout Entity representing an exercise or workout session performed by a user.
 */
@Entity
@Table(name = "workouts", indexes = {
        @Index(name = "idx_workout_user_id", columnList = "user_id"),
        @Index(name = "idx_workout_date", columnList = "workout_date"),
        @Index(name = "idx_workout_user_date", columnList = "user_id, workout_date")
})
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Workout type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "workout_type", nullable = false, length = 50)
    private WorkoutType workoutType;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than zero")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotNull(message = "Calories burned is required")
    @PositiveOrZero(message = "Calories burned must be non-negative")
    @Column(name = "calories_burned", nullable = false)
    private Integer caloriesBurned;

    @NotNull(message = "Workout date is required")
    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Workout() {
    }

    public Workout(User user, WorkoutType workoutType, Integer durationMinutes, Integer caloriesBurned, LocalDate workoutDate, String notes) {
        this.user = user;
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.workoutDate = workoutDate;
        this.notes = notes;
    }

    public Workout(Long id, User user, WorkoutType workoutType, Integer durationMinutes, Integer caloriesBurned, LocalDate workoutDate, String notes) {
        this.id = id;
        this.user = user;
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.workoutDate = workoutDate;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", workoutType=" + workoutType +
                ", durationMinutes=" + durationMinutes +
                ", caloriesBurned=" + caloriesBurned +
                ", workoutDate=" + workoutDate +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
