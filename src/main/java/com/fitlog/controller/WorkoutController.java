package com.fitlog.controller;

import com.fitlog.dto.CreateWorkoutRequest;
import com.fitlog.dto.UpdateWorkoutRequest;
import com.fitlog.dto.WorkoutResponse;
import com.fitlog.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Workout Management", description = "Endpoints for logging and retrieving workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/api/workouts")
    @Operation(summary = "Log a new workout")
    public ResponseEntity<WorkoutResponse> createWorkout(@Valid @RequestBody CreateWorkoutRequest request) {
        WorkoutResponse response = workoutService.createWorkout(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/workouts")
    @Operation(summary = "Get all logged workouts")
    public ResponseEntity<List<WorkoutResponse>> getAllWorkouts() {
        return ResponseEntity.ok(workoutService.getAllWorkouts());
    }

    @GetMapping("/api/workouts/{id}")
    @Operation(summary = "Get workout by ID")
    public ResponseEntity<WorkoutResponse> getWorkoutById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.getWorkoutById(id));
    }

    @PutMapping("/api/workouts/{id}")
    @Operation(summary = "Update an existing workout")
    public ResponseEntity<WorkoutResponse> updateWorkout(@PathVariable Long id, @Valid @RequestBody UpdateWorkoutRequest request) {
        return ResponseEntity.ok(workoutService.updateWorkout(id, request));
    }

    @DeleteMapping("/api/workouts/{id}")
    @Operation(summary = "Delete a workout")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/users/{userId}/workouts")
    @Operation(summary = "Get workouts for a user, optionally filtered by date")
    public ResponseEntity<List<WorkoutResponse>> getUserWorkouts(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date != null) {
            return ResponseEntity.ok(workoutService.getWorkoutsByUserIdAndDate(userId, date));
        }
        return ResponseEntity.ok(workoutService.getWorkoutsByUserId(userId));
    }
}
