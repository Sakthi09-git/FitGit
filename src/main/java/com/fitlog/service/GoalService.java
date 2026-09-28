package com.fitlog.service;

import com.fitlog.dto.CreateGoalRequest;
import com.fitlog.dto.GoalProgressResponse;
import com.fitlog.dto.GoalResponse;
import com.fitlog.dto.UpdateGoalRequest;
import com.fitlog.entity.Goal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing weekly workout goals and progress tracking.
 */
@Service
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final WorkoutRepository workoutRepository;
    private final UserService userService;

    @Autowired
    public GoalService(GoalRepository goalRepository, WorkoutRepository workoutRepository, UserService userService) {
        this.goalRepository = goalRepository;
        this.workoutRepository = workoutRepository;
        this.userService = userService;
    }

    public GoalResponse createGoal(CreateGoalRequest request) {
        validateGoalData(request.getWeeklyWorkoutTarget(), request.getWeekStartDate());

        User user = userService.getUserEntityById(request.getUserId());

        // Check if goal already exists for this week; if so, update it (upsert)
        Optional<Goal> existingOpt = goalRepository.findByUserIdAndWeekStartDate(user.getId(), request.getWeekStartDate());
        Goal goal;
        if (existingOpt.isPresent()) {
            goal = existingOpt.get();
            goal.setWeeklyWorkoutTarget(request.getWeeklyWorkoutTarget());
        } else {
            goal = new Goal();
            goal.setUser(user);
            goal.setWeeklyWorkoutTarget(request.getWeeklyWorkoutTarget());
            goal.setWeekStartDate(request.getWeekStartDate());
        }

        Goal savedGoal = goalRepository.save(goal);
        return GoalResponse.fromEntity(savedGoal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getAllGoals() {
        return goalRepository.findAll().stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoalById(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        return GoalResponse.fromEntity(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getGoalsByUserId(Long userId) {
        userService.getUserEntityById(userId);
        return goalRepository.findByUserIdOrderByWeekStartDateDesc(userId).stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoalByUserIdAndWeek(Long userId, LocalDate weekStartDate) {
        userService.getUserEntityById(userId);
        Goal goal = goalRepository.findByUserIdAndWeekStartDate(userId, weekStartDate)
                .orElseThrow(() -> new ResourceNotFoundException("No workout goal found for user " + userId + " for week starting " + weekStartDate));
        return GoalResponse.fromEntity(goal);
    }

    public GoalResponse updateGoal(Long id, UpdateGoalRequest request) {
        validateGoalData(request.getWeeklyWorkoutTarget(), request.getWeekStartDate());

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        goal.setWeeklyWorkoutTarget(request.getWeeklyWorkoutTarget());
        goal.setWeekStartDate(request.getWeekStartDate());

        Goal updatedGoal = goalRepository.save(goal);
        return GoalResponse.fromEntity(updatedGoal);
    }

    public void deleteGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        goalRepository.delete(goal);
    }

    @Transactional(readOnly = true)
    public GoalProgressResponse getGoalProgress(Long userId, LocalDate weekStartDate) {
        userService.getUserEntityById(userId);

        if (weekStartDate == null) {
            throw new BusinessRuleException("Week start date cannot be null.");
        }

        Goal goal = goalRepository.findByUserIdAndWeekStartDate(userId, weekStartDate)
                .orElseThrow(() -> new ResourceNotFoundException("No workout goal found for user " + userId + " for week starting " + weekStartDate));

        LocalDate weekEnd = weekStartDate.plusDays(6);
        List<Workout> workouts = workoutRepository.findByUserIdAndWorkoutDateBetween(userId, weekStartDate, weekEnd);

        int target = goal.getWeeklyWorkoutTarget();
        int completed = workouts.size();
        int remaining = Math.max(0, target - completed);
        boolean goalAchieved = completed >= target;

        return new GoalProgressResponse(userId, weekStartDate, target, completed, remaining, goalAchieved);
    }

    /**
     * Enforce core business rules:
     * - Rule 4: Weekly target must be strictly positive
     * - Rule 5: Date cannot be null
     */
    private void validateGoalData(Integer target, LocalDate weekStartDate) {
        if (target != null && target <= 0) {
            throw new BusinessRuleException("Weekly workout target must be greater than zero.");
        }
        if (weekStartDate == null) {
            throw new BusinessRuleException("Week start date cannot be null.");
        }
    }
}
