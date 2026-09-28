package com.fitlog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitlog.entity.Meal;
import com.fitlog.enums.MealType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MealResponse {

    private Long id;
    private Long userId;
    private String userName;
    private MealType mealType;
    private String foodItem;
    private String quantity;
    private Integer calories;
    private LocalDate mealDate;
    private String notes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public MealResponse() {
    }

    public MealResponse(Long id, Long userId, String userName, MealType mealType, String foodItem,
                        String quantity, Integer calories, LocalDate mealDate, String notes,
                        LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.mealType = mealType;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.calories = calories;
        this.mealDate = mealDate;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static MealResponse fromEntity(Meal meal) {
        if (meal == null) {
            return null;
        }
        return new MealResponse(
                meal.getId(),
                meal.getUser() != null ? meal.getUser().getId() : null,
                meal.getUser() != null ? meal.getUser().getName() : null,
                meal.getMealType(),
                meal.getFoodItem(),
                meal.getQuantity(),
                meal.getCalories(),
                meal.getMealDate(),
                meal.getNotes(),
                meal.getCreatedAt()
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
}
