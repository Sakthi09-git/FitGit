package com.fitlog.entity;

import com.fitlog.enums.MealType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Meal Entity representing a meal or food intake logged by a user.
 */
@Entity
@Table(name = "meals", indexes = {
        @Index(name = "idx_meal_user_id", columnList = "user_id"),
        @Index(name = "idx_meal_date", columnList = "meal_date"),
        @Index(name = "idx_meal_user_date", columnList = "user_id, meal_date")
})
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Meal type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 50)
    private MealType mealType;

    @NotBlank(message = "Food item is required")
    @Column(name = "food_item", nullable = false, length = 150)
    private String foodItem;

    @Column(length = 100)
    private String quantity;

    @NotNull(message = "Calories consumed is required")
    @PositiveOrZero(message = "Calories consumed must be non-negative")
    @Column(nullable = false)
    private Integer calories;

    @NotNull(message = "Meal date is required")
    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Meal() {
    }

    public Meal(User user, MealType mealType, String foodItem, String quantity, Integer calories, LocalDate mealDate, String notes) {
        this.user = user;
        this.mealType = mealType;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.calories = calories;
        this.mealDate = mealDate;
        this.notes = notes;
    }

    public Meal(Long id, User user, MealType mealType, String foodItem, String quantity, Integer calories, LocalDate mealDate, String notes) {
        this.id = id;
        this.user = user;
        this.mealType = mealType;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.calories = calories;
        this.mealDate = mealDate;
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

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
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
        return "Meal{" +
                "id=" + id +
                ", mealType=" + mealType +
                ", foodItem='" + foodItem + '\'' +
                ", quantity='" + quantity + '\'' +
                ", calories=" + calories +
                ", mealDate=" + mealDate +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
