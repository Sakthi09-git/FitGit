package com.fitlog.config;

import com.fitlog.entity.Goal;
import com.fitlog.entity.Meal;
import com.fitlog.entity.User;
import com.fitlog.entity.Workout;
import com.fitlog.enums.MealType;
import com.fitlog.enums.WorkoutType;
import com.fitlog.repository.GoalRepository;
import com.fitlog.repository.MealRepository;
import com.fitlog.repository.UserRepository;
import com.fitlog.repository.WorkoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds demonstration users, workouts, meals, and weekly goals into the database on startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final MealRepository mealRepository;
    private final GoalRepository goalRepository;

    public DataInitializer(UserRepository userRepository, WorkoutRepository workoutRepository,
                           MealRepository mealRepository, GoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.mealRepository = mealRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("FitLog database already populated with sample data. Skipping initialization.");
            return;
        }

        log.info("Initializing FitLog demonstration sample data...");

        // 1. Create Sample Users
        User arun = new User("Arun Kumar", "arun@example.com", "9876543210", "password123");
        User priya = new User("Priya Sharma", "priya@example.com", "9876543211", "password123");
        User demo = new User("Demo Student", "demo@example.com", "9876543212", "password123");

        userRepository.save(arun);
        userRepository.save(priya);
        userRepository.save(demo);

        LocalDate baseDate = LocalDate.of(2026, 9, 28);

        // 2. Create Sample Meals for Arun on 2026-09-28 (Sum = 1800 calories)
        mealRepository.save(new Meal(arun, MealType.BREAKFAST, "Oatmeal", "1 bowl", 300, baseDate, "With banana and honey"));
        mealRepository.save(new Meal(arun, MealType.LUNCH, "Rice and vegetables", "1 plate", 600, baseDate, "Brown rice with mixed vegetables"));
        mealRepository.save(new Meal(arun, MealType.SNACK, "Fruit", "1 bowl", 200, baseDate, "Fresh apple and almonds"));
        mealRepository.save(new Meal(arun, MealType.DINNER, "Chicken and rice", "1 plate", 700, baseDate, "Grilled chicken breast with veggies"));

        // Additional meals for Arun across the week
        mealRepository.save(new Meal(arun, MealType.BREAKFAST, "Eggs & Whole Wheat Toast", "2 eggs, 2 slices", 350, baseDate.plusDays(1), "Scrambled eggs"));
        mealRepository.save(new Meal(arun, MealType.LUNCH, "Lentil Soup and Salad", "1 bowl", 550, baseDate.plusDays(1), "High protein lunch"));
        mealRepository.save(new Meal(arun, MealType.DINNER, "Grilled Salmon with Quinoa", "1 fillet", 650, baseDate.plusDays(1), "Omega-3 rich dinner"));

        // Sample meals for Priya
        mealRepository.save(new Meal(priya, MealType.BREAKFAST, "Greek Yogurt with Berries", "1 cup", 250, baseDate, "Low-fat yogurt"));
        mealRepository.save(new Meal(priya, MealType.LUNCH, "Paneer Salad Wrap", "1 wrap", 480, baseDate, "Whole wheat wrap"));
        mealRepository.save(new Meal(priya, MealType.DINNER, "Vegetable Stir Fry with Tofu", "1 bowl", 420, baseDate, "Light dinner"));

        // 3. Create Sample Workouts for Arun on 2026-09-28 (Sum = 600 calories burned, matching prompt example)
        workoutRepository.save(new Workout(arun, WorkoutType.RUNNING, 30, 250, baseDate, "Morning outdoor run"));
        workoutRepository.save(new Workout(arun, WorkoutType.WALKING, 30, 120, baseDate, "Post-lunch brisk walk"));
        workoutRepository.save(new Workout(arun, WorkoutType.GYM, 35, 230, baseDate, "Upper body strength session"));

        // Additional workouts for Arun in the week (Total for week: 4 workouts, 150 mins, 1200 calories)
        workoutRepository.save(new Workout(arun, WorkoutType.CYCLING, 55, 600, baseDate.plusDays(2), "Evening road cycling loop"));

        // Sample workouts for Priya
        workoutRepository.save(new Workout(priya, WorkoutType.SWIMMING, 45, 350, baseDate, "Freestyle lap swimming"));
        workoutRepository.save(new Workout(priya, WorkoutType.YOGA, 45, 160, baseDate.plusDays(2), "Hatha yoga flexibility session"));

        // 4. Create Sample Goals
        // Arun: target of 5 workouts for week 2026-09-28 (has 4 completed, 1 remaining)
        goalRepository.save(new Goal(arun, 5, baseDate));

        // Priya: target of 3 workouts for week 2026-09-28 (has 2 completed, 1 remaining)
        goalRepository.save(new Goal(priya, 3, baseDate));

        log.info("FitLog sample data initialized successfully.");
    }
}
