package com.fitlog.service;

import com.fitlog.dto.CreateWorkoutRequest;
import com.fitlog.dto.WorkoutResponse;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.enums.WorkoutType;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.WorkoutRepository;
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
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WorkoutService workoutService;

    private User sampleUser;
    private CreateWorkoutRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Arun Kumar", "arun@example.com", "9876543210", "pass");
        sampleUser.setId(1L);

        validRequest = new CreateWorkoutRequest(
                1L,
                WorkoutType.RUNNING,
                30,
                250,
                LocalDate.of(2026, 9, 28),
                "Morning run"
        );
    }

    @Test
    @DisplayName("Should successfully create a workout")
    void createWorkout_Success() {
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);

        Workout savedWorkout = new Workout(
                100L,
                sampleUser,
                WorkoutType.RUNNING,
                30,
                250,
                LocalDate.of(2026, 9, 28),
                "Morning run"
        );
        savedWorkout.setCreatedAt(LocalDateTime.now());
        when(workoutRepository.save(any(Workout.class))).thenReturn(savedWorkout);

        WorkoutResponse response = workoutService.createWorkout(validRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(250, response.getCaloriesBurned());
        assertEquals(30, response.getDurationMinutes());
        assertEquals(WorkoutType.RUNNING, response.getWorkoutType());
        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    @DisplayName("RULE 1: Should reject negative calories burned with 400 BusinessRuleException")
    void createWorkout_NegativeCalories_ThrowsBusinessRuleException() {
        validRequest.setCaloriesBurned(-100);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                workoutService.createWorkout(validRequest));

        assertEquals("Calories burned must be a non-negative value.", ex.getMessage());
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    @DisplayName("RULE 3: Should reject duration <= 0 with BusinessRuleException")
    void createWorkout_ZeroOrNegativeDuration_ThrowsBusinessRuleException() {
        validRequest.setDurationMinutes(0);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                workoutService.createWorkout(validRequest));

        assertEquals("Duration must be greater than zero.", ex.getMessage());

        validRequest.setDurationMinutes(-15);
        assertThrows(BusinessRuleException.class, () ->
                workoutService.createWorkout(validRequest));
    }

    @Test
    @DisplayName("RULE 5: Should reject null workout date")
    void createWorkout_NullDate_ThrowsBusinessRuleException() {
        validRequest.setWorkoutDate(null);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                workoutService.createWorkout(validRequest));

        assertEquals("Workout date cannot be null.", ex.getMessage());
    }

    @Test
    @DisplayName("RULE 6: Should throw ResourceNotFoundException for non-existent user")
    void createWorkout_UserNotFound_ThrowsException() {
        when(userService.getUserEntityById(999L)).thenThrow(new ResourceNotFoundException("User not found with id: 999"));
        validRequest.setUserId(999L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                workoutService.createWorkout(validRequest));

        assertTrue(ex.getMessage().contains("User not found"));
    }
}
