package com.fitlog.controller;

import com.fitlog.dto.CreateMealRequest;
import com.fitlog.dto.MealResponse;
import com.fitlog.dto.UpdateMealRequest;
import com.fitlog.service.MealService;
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
@Tag(name = "Meal Management", description = "Endpoints for logging and tracking meals and calories")
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping("/api/meals")
    @Operation(summary = "Log a new meal")
    public ResponseEntity<MealResponse> createMeal(@Valid @RequestBody CreateMealRequest request) {
        MealResponse response = mealService.createMeal(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/meals")
    @Operation(summary = "Get all logged meals")
    public ResponseEntity<List<MealResponse>> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @GetMapping("/api/meals/{id}")
    @Operation(summary = "Get meal by ID")
    public ResponseEntity<MealResponse> getMealById(@PathVariable Long id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @PutMapping("/api/meals/{id}")
    @Operation(summary = "Update an existing meal")
    public ResponseEntity<MealResponse> updateMeal(@PathVariable Long id, @Valid @RequestBody UpdateMealRequest request) {
        return ResponseEntity.ok(mealService.updateMeal(id, request));
    }

    @DeleteMapping("/api/meals/{id}")
    @Operation(summary = "Delete a meal")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/users/{userId}/meals")
    @Operation(summary = "Get meals for a user, optionally filtered by date")
    public ResponseEntity<List<MealResponse>> getUserMeals(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date != null) {
            return ResponseEntity.ok(mealService.getMealsByUserIdAndDate(userId, date));
        }
        return ResponseEntity.ok(mealService.getMealsByUserId(userId));
    }
}
