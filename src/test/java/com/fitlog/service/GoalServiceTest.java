package com.fitlog.service;

import com.fitlog.dto.CreateGoalRequest;
import com.fitlog.dto.GoalProgressResponse;
import com.fitlog.dto.GoalResponse;
import com.fitlog.entity.Goal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.enums.WorkoutType;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.GoalRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GoalService goalService;

    private User sampleUser;
    private LocalDate weekStart;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Arun Kumar", "arun@example.com", "9876543210", "pass");
        sampleUser.setId(1L);
        weekStart = LocalDate.of(2026, 9, 28);
    }

    @Test
    @DisplayName("Should successfully create/upsert a weekly goal")
    void createGoal_Success() {
        CreateGoalRequest request = new CreateGoalRequest(1L, 5, weekStart);

        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);
        when(goalRepository.findByUserIdAndWeekStartDate(1L, weekStart)).thenReturn(Optional.empty());

        Goal savedGoal = new Goal(300L, sampleUser, 5, weekStart);
        savedGoal.setCreatedAt(LocalDateTime.now());
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        GoalResponse response = goalService.createGoal(request);

        assertNotNull(response);
        assertEquals(300L, response.getId());
        assertEquals(5, response.getWeeklyWorkoutTarget());
        assertEquals(weekStart, response.getWeekStartDate());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("RULE 4: Should reject weekly workout target <= 0")
    void createGoal_ZeroOrNegativeTarget_ThrowsBusinessRuleException() {
        CreateGoalRequest zeroRequest = new CreateGoalRequest(1L, 0, weekStart);

        BusinessRuleException ex1 = assertThrows(BusinessRuleException.class, () ->
                goalService.createGoal(zeroRequest));
        assertEquals("Weekly workout target must be greater than zero.", ex1.getMessage());

        CreateGoalRequest negRequest = new CreateGoalRequest(1L, -3, weekStart);
        assertThrows(BusinessRuleException.class, () ->
                goalService.createGoal(negRequest));
    }

    @Test
    @DisplayName("Feature 5: Should accurately calculate goal progress")
    void getGoalProgress_Success() {
        Goal goal = new Goal(300L, sampleUser, 5, weekStart);
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);
        when(goalRepository.findByUserIdAndWeekStartDate(1L, weekStart)).thenReturn(Optional.of(goal));

        // Arun has 3 workouts in this week
        List<Workout> weekWorkouts = List.of(
                new Workout(sampleUser, WorkoutType.RUNNING, 30, 250, weekStart, "Run"),
                new Workout(sampleUser, WorkoutType.WALKING, 30, 120, weekStart, "Walk"),
                new Workout(sampleUser, WorkoutType.GYM, 45, 300, weekStart.plusDays(1), "Gym")
        );
        when(workoutRepository.findByUserIdAndWorkoutDateBetween(1L, weekStart, weekStart.plusDays(6)))
                .thenReturn(weekWorkouts);

        GoalProgressResponse progress = goalService.getGoalProgress(1L, weekStart);

        assertNotNull(progress);
        assertEquals(1L, progress.getUserId());
        assertEquals(weekStart, progress.getWeekStart());
        assertEquals(5, progress.getTarget());
        assertEquals(3, progress.getCompleted());
        assertEquals(2, progress.getRemaining());
        assertFalse(progress.getGoalAchieved());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no goal exists for that week")
    void getGoalProgress_NoGoal_ThrowsException() {
        when(userService.getUserEntityById(1L)).thenReturn(sampleUser);
        when(goalRepository.findByUserIdAndWeekStartDate(1L, weekStart)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                goalService.getGoalProgress(1L, weekStart));

        assertTrue(ex.getMessage().contains("No workout goal found"));
    }
}
