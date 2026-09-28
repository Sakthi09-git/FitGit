package com.fitlog.service;

import com.fitlog.dto.CreateWorkoutRequest;
import com.fitlog.dto.UpdateWorkoutRequest;
import com.fitlog.dto.WorkoutResponse;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.exception.BusinessRuleException;
import com.fitlog.exception.ResourceNotFoundException;
import com.fitlog.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing workout sessions and enforcing business rules.
 */
@Service
@Transactional
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserService userService;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, UserService userService) {
        this.workoutRepository = workoutRepository;
        this.userService = userService;
    }

    public WorkoutResponse createWorkout(CreateWorkoutRequest request) {
        validateWorkoutData(request.getDurationMinutes(), request.getCaloriesBurned(), request.getWorkoutDate());

        User user = userService.getUserEntityById(request.getUserId());

        Workout workout = new Workout();
        workout.setUser(user);
        workout.setWorkoutType(request.getWorkoutType());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setCaloriesBurned(request.getCaloriesBurned());
        workout.setWorkoutDate(request.getWorkoutDate());
        workout.setNotes(request.getNotes());

        Workout savedWorkout = workoutRepository.save(workout);
        return WorkoutResponse.fromEntity(savedWorkout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponse> getAllWorkouts() {
        return workoutRepository.findAll().stream()
                .map(WorkoutResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutResponse getWorkoutById(Long id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        return WorkoutResponse.fromEntity(workout);
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponse> getWorkoutsByUserId(Long userId) {
        userService.getUserEntityById(userId); // Ensure user exists
        return workoutRepository.findByUserId(userId).stream()
                .map(WorkoutResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkoutResponse> getWorkoutsByUserIdAndDate(Long userId, LocalDate workoutDate) {
        userService.getUserEntityById(userId); // Ensure user exists
        if (workoutDate == null) {
            return getWorkoutsByUserId(userId);
        }
        return workoutRepository.findByUserIdAndWorkoutDate(userId, workoutDate).stream()
                .map(WorkoutResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public WorkoutResponse updateWorkout(Long id, UpdateWorkoutRequest request) {
        validateWorkoutData(request.getDurationMinutes(), request.getCaloriesBurned(), request.getWorkoutDate());

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));

        workout.setWorkoutType(request.getWorkoutType());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setCaloriesBurned(request.getCaloriesBurned());
        workout.setWorkoutDate(request.getWorkoutDate());
        workout.setNotes(request.getNotes());

        Workout updatedWorkout = workoutRepository.save(workout);
        return WorkoutResponse.fromEntity(updatedWorkout);
    }

    public void deleteWorkout(Long id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));
        workoutRepository.delete(workout);
    }

    /**
     * Enforce core business rules:
     * - Rule 1: Calories burned must be non-negative
     * - Rule 3: Duration must be strictly positive
     * - Rule 5: Date cannot be null
     */
    private void validateWorkoutData(Integer durationMinutes, Integer caloriesBurned, LocalDate workoutDate) {
        if (caloriesBurned != null && caloriesBurned < 0) {
            throw new BusinessRuleException("Calories burned must be a non-negative value.");
        }
        if (durationMinutes != null && durationMinutes <= 0) {
            throw new BusinessRuleException("Duration must be greater than zero.");
        }
        if (workoutDate == null) {
            throw new BusinessRuleException("Workout date cannot be null.");
        }
    }
}
