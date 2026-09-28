package com.fitlog.service;

import com.fitlog.dto.CreateMealRequest;
import com.fitlog.dto.MealResponse;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.enums.MealType;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MealService mealService;

    private User sampleUser;
    private CreateMealRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Arun Kumar", "arun@example.com", "9876543210", "pass");
        sampleUser.setId(1L);

        validRequest = new CreateMealRequest(
                1L,
                MealType.BREAKFAST,
                "Oatmeal",
                "1 bowl",
                300,
                LocalDate.of(2026, 9, 28),
                "With banana"
        );
    }

    @Test
    @DisplayName("Should successfully create a meal")
    void createMeal_Success() {
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);

        Meal savedMeal = new Meal(
                200L,
                sampleUser,
                MealType.BREAKFAST,
                "Oatmeal",
                "1 bowl",
                300,
                LocalDate.of(2026, 9, 28),
                "With banana"
        );
        savedMeal.setCreatedAt(LocalDateTime.now());
        when(mealRepository.save(any(Meal.class))).thenReturn(savedMeal);

        MealResponse response = mealService.createMeal(validRequest);

        assertNotNull(response);
        assertEquals(200L, response.getId());
        assertEquals("Oatmeal", response.getFoodItem());
        assertEquals(300, response.getCalories());
        assertEquals(MealType.BREAKFAST, response.getMealType());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    @DisplayName("RULE 1: Should reject negative calories consumed with 400 BusinessRuleException")
    void createMeal_NegativeCalories_ThrowsBusinessRuleException() {
        validRequest.setCalories(-500);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                mealService.createMeal(validRequest));

        assertEquals("Calories consumed must be a non-negative value.", ex.getMessage());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    @DisplayName("Should reject empty food item")
    void createMeal_EmptyFoodItem_ThrowsBusinessRuleException() {
        validRequest.setFoodItem("   ");

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                mealService.createMeal(validRequest));

        assertEquals("Food item cannot be empty.", ex.getMessage());
    }

    @Test
    @DisplayName("RULE 5: Should reject null meal date")
    void createMeal_NullDate_ThrowsBusinessRuleException() {
        validRequest.setMealDate(null);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                mealService.createMeal(validRequest));

        assertEquals("Meal date cannot be null.", ex.getMessage());
    }

    @Test
    @DisplayName("RULE 6: Should throw ResourceNotFoundException when user does not exist")
    void createMeal_UserNotFound_ThrowsException() {
        when(userService.getUserEntityById(999L)).thenThrow(new ResourceNotFoundException("User not found with id: 999"));
        validRequest.setUserId(999L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                mealService.createMeal(validRequest));

        assertTrue(ex.getMessage().contains("User not found"));
    }
}
