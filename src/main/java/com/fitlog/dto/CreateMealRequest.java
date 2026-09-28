package com.fitlog.dto;

import com.fitlog.enums.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class CreateMealRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotBlank(message = "Food item is required")
    private String foodItem;

    private String quantity;

    @NotNull(message = "Calories consumed is required")
    @PositiveOrZero(message = "Calories consumed must be non-negative")
    private Integer calories;

    @NotNull(message = "Meal date is required")
    private LocalDate mealDate;

    private String notes;

    public CreateMealRequest() {
    }

    public CreateMealRequest(Long userId, MealType mealType, String foodItem, String quantity, Integer calories, LocalDate mealDate, String notes) {
        this.userId = userId;
        this.mealType = mealType;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.calories = calories;
        this.mealDate = mealDate;
        this.notes = notes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
