package com.fitlog.repository;

import com.fitlog.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA Repository for Meal entity.
 */
@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserId(Long userId);

    List<Meal> findByUserIdAndMealDate(Long userId, LocalDate mealDate);

    List<Meal> findByUserIdAndMealDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<Meal> findByUserIdAndMealDateBetweenOrderByMealDateAsc(Long userId, LocalDate startDate, LocalDate endDate);
}
