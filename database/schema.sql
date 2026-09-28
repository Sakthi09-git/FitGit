-- ==============================================================================
-- FitLog — Personal Workout and Calorie Tracker
-- MySQL Database Schema Definition
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `fitlog_db`
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE `fitlog_db`;

-- 1. Table: users
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) DEFAULT NULL,
    `password` VARCHAR(255) DEFAULT NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Table: workouts
CREATE TABLE IF NOT EXISTS `workouts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `workout_type` VARCHAR(50) NOT NULL,
    `duration_minutes` INT NOT NULL,
    `calories_burned` INT NOT NULL,
    `workout_date` DATE NOT NULL,
    `notes` VARCHAR(500) DEFAULT NULL,
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_workouts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_workout_user_id` (`user_id`),
    INDEX `idx_workout_date` (`workout_date`),
    INDEX `idx_workout_user_date` (`user_id`, `workout_date`),
    CONSTRAINT `chk_workout_calories` CHECK (`calories_burned` >= 0),
    CONSTRAINT `chk_workout_duration` CHECK (`duration_minutes` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Table: meals
CREATE TABLE IF NOT EXISTS `meals` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `meal_type` VARCHAR(50) NOT NULL,
    `food_item` VARCHAR(150) NOT NULL,
    `quantity` VARCHAR(100) DEFAULT NULL,
    `calories` INT NOT NULL,
    `meal_date` DATE NOT NULL,
    `notes` VARCHAR(500) DEFAULT NULL,
    `created_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_meals_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_meal_user_id` (`user_id`),
    INDEX `idx_meal_date` (`meal_date`),
    INDEX `idx_meal_user_date` (`user_id`, `meal_date`),
    CONSTRAINT `chk_meal_calories` CHECK (`calories` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Table: goals
CREATE TABLE IF NOT EXISTS `goals` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `weekly_workout_target` INT NOT NULL,
    `week_start_date` DATE NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_goals_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_goal_user_id` (`user_id`),
    INDEX `idx_goal_week_start` (`week_start_date`),
    INDEX `idx_goal_user_week` (`user_id`, `week_start_date`),
    CONSTRAINT `chk_goal_target` CHECK (`weekly_workout_target` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
