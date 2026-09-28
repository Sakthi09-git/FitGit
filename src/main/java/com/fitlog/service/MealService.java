package com.fitlog.service;

import com.fitlog.dto.CreateMealRequest;
import com.fitlog.dto.MealResponse;
import com.fitlog.dto.UpdateMealRequest;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing meals and calorie intake records.
 */
@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final UserService userService;

    @Autowired
    public MealService(MealRepository mealRepository, UserService userService) {
        this.mealRepository = mealRepository;
        this.userService = userService;
    }

    public MealResponse createMeal(CreateMealRequest request) {
        validateMealData(request.getCalories(), request.getMealDate(), request.getFoodItem());

        User user = userService.getUserEntityById(request.getUserId());

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setMealType(request.getMealType());
        meal.setFoodItem(request.getFoodItem().trim());
        meal.setQuantity(request.getQuantity());
        meal.setCalories(request.getCalories());
        meal.setMealDate(request.getMealDate());
        meal.setNotes(request.getNotes());

        Meal savedMeal = mealRepository.save(meal);
        return MealResponse.fromEntity(savedMeal);
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getAllMeals() {
        return mealRepository.findAll().stream()
                .map(MealResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MealResponse getMealById(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + id));
        return MealResponse.fromEntity(meal);
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getMealsByUserId(Long userId) {
        userService.getUserEntityById(userId); // Ensure user exists
        return mealRepository.findByUserId(userId).stream()
                .map(MealResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getMealsByUserIdAndDate(Long userId, LocalDate mealDate) {
        userService.getUserEntityById(userId); // Ensure user exists
        if (mealDate == null) {
            return getMealsByUserId(userId);
        }
        return mealRepository.findByUserIdAndMealDate(userId, mealDate).stream()
                .map(MealResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public MealResponse updateMeal(Long id, UpdateMealRequest request) {
        validateMealData(request.getCalories(), request.getMealDate(), request.getFoodItem());

        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + id));

        meal.setMealType(request.getMealType());
        meal.setFoodItem(request.getFoodItem().trim());
        meal.setQuantity(request.getQuantity());
        meal.setCalories(request.getCalories());
        meal.setMealDate(request.getMealDate());
        meal.setNotes(request.getNotes());

        Meal updatedMeal = mealRepository.save(meal);
        return MealResponse.fromEntity(updatedMeal);
    }

    public void deleteMeal(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + id));
        mealRepository.delete(meal);
    }

    /**
     * Enforce core business rules:
     * - Rule 1: Calories consumed must be non-negative
     * - Rule 5: Date cannot be null
     */
    private void validateMealData(Integer calories, LocalDate mealDate, String foodItem) {
        if (calories != null && calories < 0) {
            throw new BusinessRuleException("Calories consumed must be a non-negative value.");
        }
        if (mealDate == null) {
            throw new BusinessRuleException("Meal date cannot be null.");
        }
        if (foodItem == null || foodItem.trim().isEmpty()) {
            throw new BusinessRuleException("Food item cannot be empty.");
        }
    }
}
