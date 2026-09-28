package com.fitlog.controller;

import com.fitlog.dto.CreateGoalRequest;
import com.fitlog.dto.GoalProgressResponse;
import com.fitlog.dto.GoalResponse;
import com.fitlog.dto.UpdateGoalRequest;
import com.fitlog.service.GoalService;
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
@Tag(name = "Goal Management", description = "Endpoints for setting and monitoring weekly workout goals")
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/api/goals")
    @Operation(summary = "Create or set a weekly workout goal")
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        GoalResponse response = goalService.createGoal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/goals")
    @Operation(summary = "Get all goals")
    public ResponseEntity<List<GoalResponse>> getAllGoals() {
        return ResponseEntity.ok(goalService.getAllGoals());
    }

    @GetMapping("/api/goals/{id}")
    @Operation(summary = "Get goal by ID")
    public ResponseEntity<GoalResponse> getGoalById(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.getGoalById(id));
    }

    @PutMapping("/api/goals/{id}")
    @Operation(summary = "Update an existing goal")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @Valid @RequestBody UpdateGoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(id, request));
    }

    @DeleteMapping("/api/goals/{id}")
    @Operation(summary = "Delete a goal")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/users/{userId}/goals")
    @Operation(summary = "Get user goals, optionally filtered by week start date")
    public ResponseEntity<?> getUserGoals(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        if (weekStart != null) {
            return ResponseEntity.ok(goalService.getGoalByUserIdAndWeek(userId, weekStart));
        }
        return ResponseEntity.ok(goalService.getGoalsByUserId(userId));
    }

    @GetMapping("/api/users/{userId}/goals/progress")
    @Operation(summary = "Check progress against weekly workout target")
    public ResponseEntity<GoalProgressResponse> getGoalProgress(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return ResponseEntity.ok(goalService.getGoalProgress(userId, weekStart));
    }
}
