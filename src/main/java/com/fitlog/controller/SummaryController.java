package com.fitlog.controller;

import com.fitlog.dto.DailySummaryResponse;
import com.fitlog.dto.WeeklySummaryResponse;
import com.fitlog.dto.WeeklyWorkoutTrendResponse;
import com.fitlog.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Tag(name = "Summary & Trends", description = "Endpoints for daily calorie balances, weekly summaries, and workout trends")
@CrossOrigin(origins = "*")
public class SummaryController {

    private final SummaryService summaryService;

    @Autowired
    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/api/users/{userId}/summary/daily")
    @Operation(summary = "Get daily calorie summary (Calories In, Calories Out, Net Calories)")
    public ResponseEntity<DailySummaryResponse> getDailySummary(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(summaryService.getDailyCalorieSummary(userId, date));
    }

    @GetMapping("/api/users/{userId}/summary/weekly")
    @Operation(summary = "Get weekly summary of calories and activities (enforces Rule 2)")
    public ResponseEntity<WeeklySummaryResponse> getWeeklySummary(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return ResponseEntity.ok(summaryService.getWeeklySummary(userId, weekStart));
    }

    @GetMapping("/api/users/{userId}/workouts/weekly")
    @Operation(summary = "Get weekly workout trend showing workout count, duration, and calories burned")
    public ResponseEntity<WeeklyWorkoutTrendResponse> getWeeklyWorkoutTrend(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return ResponseEntity.ok(summaryService.getWeeklyWorkoutTrend(userId, weekStart));
    }
}
