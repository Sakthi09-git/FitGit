# FitLog — Personal Workout and Calorie Tracker

A complete, production-ready Java Spring Boot &amp; MySQL application built for college **Java Programming + DBMS Practical / Project Assessment**.

---

## 1. Official Project Title
**FitLog — Personal Workout and Calorie Tracker**

---

## 2. Official Problem Statement
Fitness beginners struggle to maintain a consistent log of their daily workouts and caloric intake because manual paper diaries and unstructured spreadsheets are prone to being abandoned or forgotten.

This project delivers a robust backend and dashboard enabling fitness beginners to:
- Log daily workouts with duration, workout type, and calories burned
- Log daily meals with meal type, food items, quantity, and calories consumed
- Track real-time daily calories-in versus calories-out (net caloric balance)
- Visualize weekly workout trends (count, duration, and calories burned)
- Set and monitor a personal weekly workout-count goal with live progress tracking

---

## 3. Objective
FitLog bridges the gap between daily fitness tracking and actionable health insights by enforcing strict service-layer data validation, calculating dynamic net calorie balances from persistent relational records, and giving users clear weekly visibility without requiring complex third-party apps or paper logs.

---

## 4. Core Features

### Feature 1 — Log Workout
Users can log daily exercises specifying:
- **Workout Type**: `RUNNING`, `WALKING`, `CYCLING`, `GYM`, `YOGA`, `SWIMMING`, `OTHER`
- **Duration**: Duration in minutes (must be `> 0`)
- **Calories Burned**: Estimated calories burned (strictly non-negative)
- **Workout Date**: Valid calendar date
- **Notes**: Optional notes regarding the session

### Feature 2 — Log Meal
Users can record food intake specifying:
- **Meal Type**: `BREAKFAST`, `LUNCH`, `DINNER`, `SNACK`, `OTHER`
- **Food Item**: Name/description of dish
- **Quantity**: Portion size (e.g. `1 bowl`, `2 slices`)
- **Calories**: Caloric value (strictly non-negative)
- **Meal Date**: Valid calendar date
- **Notes**: Optional dietary or preparation details

### Feature 3 — Daily Calorie Summary
Computes live caloric balance for a selected user and date:
$$\text{Net Calories} = \text{Calories Consumed (Meals)} - \text{Calories Burned (Workouts)}$$
- Automatically aggregates all meals and workouts logged on that calendar day directly from MySQL records.

### Feature 4 — Weekly Workout Trend
Generates a 7-day retrospective showing:
- Total workouts completed
- Total active duration (minutes)
- Total calories burned
- Day-by-day trend breakdown across the entire week

### Feature 5 — Weekly Workout Goal & Progress
Allows users to set a weekly workout target (e.g., 5 sessions per week) and provides an automated progress tracker indicating:
- Weekly target
- Workouts completed so far
- Remaining workouts required
- Boolean flag: `goalAchieved` (`completed >= target`)

---

## 5. Technology Stack

- **Backend Platform**: Java 17 (LTS)
- **Framework**: Spring Boot 3.3.4
- **Modules**:
  - Spring Web (RESTful APIs)
  - Spring Data JPA (Relational persistence)
  - Hibernate 6.x (ORM provider)
  - Spring Validation (Jakarta Bean Validation API)
  - Jackson (JSON serialization & JSR-310 date/time mapping)
- **Database**: MySQL 8.0+ (MySQL Connector/J)
- **API Documentation**: Springdoc OpenAPI 2.6.0 (Swagger UI)
- **Testing**: JUnit 5, Spring Boot Test, MockMvc, H2 (test scope)
- **Frontend**: Semantic HTML5, CSS3 (Glassmorphism Dark Theme), Vanilla JavaScript (ES6+), Fetch API (No React / Angular / external UI frameworks)

---

## 6. System Architecture

FitLog implements a clean 5-layer enterprise architecture:

```
[ Frontend Dashboard / Postman / Swagger UI ]
                     │  HTTP / JSON
                     ▼
           [ REST Controllers ]
                     │  DTOs
                     ▼
            [ Service Layer ]  <── Enforces Business Rules 1–7
                     │  Entities
                     ▼
         [ JPA Repositories Layer ]
                     │  SQL Queries / Dialect
                     ▼
              [ MySQL Database ]
```

1. **Client / Presentation Layer**: Modern glassmorphism dashboard running entirely in browser using Vanilla JS `fetch()`.
2. **REST Controller Layer**: Maps HTTP methods (`GET`, `POST`, `PUT`, `DELETE`), executes `@Valid` bean validation, and emits JSON responses. Contains **zero** business logic.
3. **Service Layer**: Houses the business rules engine. Enforces validation rules, prevents negative numbers, computes net balances, and executes transactions (`@Transactional`).
4. **Data Access Layer**: Spring Data JPA repositories interfacing with Hibernate ORM.
5. **Database Layer**: Relational MySQL schema with primary keys, foreign key constraints (`ON DELETE CASCADE`), and performance indexes.

---

## 7. Database Design & Tables

The schema is defined in `database/schema.sql` and mapped via JPA entities:

- **`users`**: User profiles (`id`, `name`, `email`, `phone`, `password`, `active`, `created_at`)
- **`workouts`**: Exercise logs (`id`, `user_id`, `workout_type`, `duration_minutes`, `calories_burned`, `workout_date`, `notes`, `created_at`)
- **`meals`**: Nutrition logs (`id`, `user_id`, `meal_type`, `food_item`, `quantity`, `calories`, `meal_date`, `notes`, `created_at`)
- **`goals`**: Weekly targets (`id`, `user_id`, `weekly_workout_target`, `week_start_date`, `created_at`, `updated_at`)

---

## 8. Entity Relationships

```
USERS (1) ───< WORKOUTS (N)
USERS (1) ───< MEALS (N)
USERS (1) ───< GOALS (N)
```

- Each **Workout** belongs to one **User** (`@ManyToOne @JoinColumn(name = "user_id")`).
- Each **Meal** belongs to one **User** (`@ManyToOne @JoinColumn(name = "user_id")`).
- Each **Goal** belongs to one **User** (`@ManyToOne @JoinColumn(name = "user_id")`).

---

## 9. Mandatory Business Rules

### MANDATORY RULE 1 — CALORIES MUST BE NON-NEGATIVE
- **Requirement**: Calories burned and calories consumed must never be negative.
- **Enforcement**:
  1. **Bean Validation**: `@PositiveOrZero` on DTO fields.
  2. **Service Layer**: Explicit inspection in `WorkoutService.validateWorkoutData()` and `MealService.validateMealData()`. If `< 0`, throws `BusinessRuleException`.
  3. **Database Layer**: `CHECK (calories_burned >= 0)` and `CHECK (calories >= 0)`.
- **Response**: `HTTP 400 Bad Request` with message: `"Calories burned must be a non-negative value."` or `"Calories consumed must be a non-negative value."`.

### MANDATORY RULE 2 — WEEKLY SUMMARY REQUIRES DATA
- **Requirement**: A weekly summary must only be generated when at least one relevant workout or meal entry exists for that week.
- **Enforcement**: In `SummaryService.getWeeklySummary(userId, weekStart)`:
  ```java
  if (workouts.isEmpty() && meals.isEmpty()) {
      throw new ResourceNotFoundException("No workout or meal entries found for the selected week.");
  }
  ```
- **Response**: `HTTP 404 NOT FOUND` with message: `"No workout or meal entries found for the selected week."`.

### ADDITIONAL SENSIBLE RULES
- **Rule 3**: Workout duration must be strictly positive (`durationMinutes > 0`).
- **Rule 4**: Weekly workout target must be strictly positive (`weeklyWorkoutTarget > 0`).
- **Rule 5**: Workout date, meal date, and week start date cannot be null.
- **Rule 6**: Cannot create entries for a non-existent user (`HTTP 404 NOT FOUND`).
- **Rule 7**: Email uniqueness enforced during user creation (`HTTP 400` / `409`).

---

## 10. REST API Documentation

### User Endpoints (`/api/users`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users` | Register a new user |
| `GET` | `/api/users` | Retrieve all users |
| `GET` | `/api/users/{id}` | Retrieve user by ID |
| `PUT` | `/api/users/{id}` | Update existing user |
| `DELETE` | `/api/users/{id}` | Delete user |

### Workout Endpoints (`/api/workouts`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/workouts` | Log a new workout |
| `GET` | `/api/workouts` | Retrieve all workouts |
| `GET` | `/api/workouts/{id}` | Retrieve workout by ID |
| `PUT` | `/api/workouts/{id}` | Update existing workout |
| `DELETE` | `/api/workouts/{id}` | Delete workout |
| `GET` | `/api/users/{userId}/workouts` | Retrieve user workouts |
| `GET` | `/api/users/{userId}/workouts?date=YYYY-MM-DD` | Filter workouts by date |

### Meal Endpoints (`/api/meals`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/meals` | Log a new meal |
| `GET` | `/api/meals` | Retrieve all meals |
| `GET` | `/api/meals/{id}` | Retrieve meal by ID |
| `PUT` | `/api/meals/{id}` | Update existing meal |
| `DELETE` | `/api/meals/{id}` | Delete meal |
| `GET` | `/api/users/{userId}/meals` | Retrieve user meals |
| `GET` | `/api/users/{userId}/meals?date=YYYY-MM-DD` | Filter meals by date |

### Goal Endpoints (`/api/goals`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/goals` | Set / Upsert weekly workout goal |
| `GET` | `/api/goals` | Retrieve all goals |
| `GET` | `/api/goals/{id}` | Retrieve goal by ID |
| `PUT` | `/api/goals/{id}` | Update existing goal |
| `DELETE` | `/api/goals/{id}` | Delete goal |
| `GET` | `/api/users/{userId}/goals?weekStart=YYYY-MM-DD` | Retrieve goal for user & week |
| `GET` | `/api/users/{userId}/goals/progress?weekStart=YYYY-MM-DD` | Calculate weekly goal progress |

### Summary & Trend Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/users/{userId}/summary/daily?date=YYYY-MM-DD` | Daily Calories In, Out, and Net Balance |
| `GET` | `/api/users/{userId}/summary/weekly?weekStart=YYYY-MM-DD` | Weekly summary (Enforces Mandatory Rule 2) |
| `GET` | `/api/users/{userId}/workouts/weekly?weekStart=YYYY-MM-DD` | Weekly workout trend & daily distribution |

---

## 11. Project Directory Structure

```
fitlog/
├── pom.xml                                   # Maven build configuration
├── README.md                                 # Comprehensive documentation
├── .gitignore                                # Git ignore file
│
├── postman/
│   ├── FitLog-API.postman_collection.json    # Complete Postman test collection
│   └── FitLog.postman_environment.json       # Postman environment configuration
│
├── database/
│   ├── README.md                             # Database design & setup guide
│   └── schema.sql                            # Direct DDL SQL schema
│
├── docs/
│   ├── architecture.md                       # Multi-tier architecture diagram
│   ├── class-diagram.md                      # Detailed class diagrams
│   └── database-schema.md                    # ER diagram and relational constraints
│
└── src/
    ├── main/
    │   ├── java/com/fitlog/
    │   │   ├── FitLogApplication.java        # Spring Boot main class
    │   │   ├── config/
    │   │   │   ├── DataInitializer.java      # CommandLineRunner demo data seeder
    │   │   │   └── OpenApiConfig.java        # Swagger UI configuration
    │   │   ├── controller/
    │   │   │   ├── UserController.java
    │   │   │   ├── WorkoutController.java
    │   │   │   ├── MealController.java
    │   │   │   ├── GoalController.java
    │   │   │   └── SummaryController.java
    │   │   ├── dto/                          # Requests, Responses, and Error DTOs
    │   │   ├── entity/                       # JPA entities (User, Workout, Meal, Goal)
    │   │   ├── enums/                        # WorkoutType, MealType
    │   │   ├── exception/                    # GlobalExceptionHandler, Custom exceptions
    │   │   ├── repository/                   # Spring Data JPA Repositories
    │   │   └── service/                      # Business rule validation services
    │   │
    │   └── resources/
    │       ├── application.properties        # MySQL configuration
    │       ├── application-example.properties# Template configuration
    │       └── static/                       # Frontend web dashboard
    │           ├── index.html
    │           ├── style.css
    │           └── app.js
    │
    └── test/
        ├── java/com/fitlog/
        │   ├── FitLogApplicationTests.java   # Context load test
        │   ├── FitLogIntegrationTest.java    # MockMvc end-to-end integration test
        │   └── service/                      # Unit tests for all business rules
        └── resources/
            └── application.properties        # H2 in-memory MySQL mode test properties
```

---

## 12. MySQL Setup Instructions

1. Start your local MySQL service:
   ```bash
   sudo systemctl start mysql
   ```
2. Verify MySQL is accessible:
   ```bash
   mysql -u root -p
   ```
3. The application will **automatically create** `fitlog_db` if it does not already exist via the connection parameter `createDatabaseIfNotExist=true`.
4. If your MySQL root password is not `root`, configure it via environment variables or edit `src/main/resources/application.properties`:
   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   ```

---

## 13. IntelliJ IDEA Setup Guide

1. Launch **IntelliJ IDEA**.
2. Click **File -> Open...** and select the `fitlog` directory.
3. Ensure **Project SDK** is set to **JDK 17** (`File -> Project Structure -> Project -> SDK`).
4. Wait for Maven to synchronize dependencies automatically.
5. In the Project tool window, navigate to:
   `src/main/java/com/fitlog/FitLogApplication.java`
6. Right-click and choose **Run 'FitLogApplication'**.
7. The application starts up on `http://localhost:8080`.

---

## 14. Maven Commands Reference

```bash
# Clean target build directory
mvn clean

# Run all unit and integration tests
mvn test

# Clean, compile, and execute tests
mvn clean test

# Package runnable JAR file
mvn clean package

# Run Spring Boot application directly from terminal
mvn spring-boot:run
```

---

## 15. Application URLs
- **Web Dashboard**: [http://localhost:8080/](http://localhost:8080/)
- **Swagger UI API Docs**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 16. Postman Testing Guide & Execution Order

Import `postman/FitLog-API.postman_collection.json` and `postman/FitLog.postman_environment.json` into Postman.

Recommended execution order:
1. **User APIs**:
   - `POST Create User`
   - `GET All Users`
   - `GET User By ID`
2. **Workout APIs**:
   - `POST Create Workout`
   - `GET All Workouts`
   - `GET User Workouts`
   - `GET Workouts By Date`
3. **Meal APIs**:
   - `POST Create Meal`
   - `GET All Meals`
   - `GET User Meals`
4. **Goal APIs**:
   - `POST Create Goal`
   - `GET Goal By ID`
   - `GET Goal Progress`
5. **Summary APIs**:
   - `GET Daily Calorie Summary`
   - `GET Weekly Summary`
   - `GET Weekly Workout Trend`
6. **Negative Test Cases**:
   - Verify negative workout calories returns `400 Bad Request`
   - Verify negative meal calories returns `400 Bad Request`
   - Verify empty week summary returns `404 Not Found` (Mandatory Rule 2)
   - Verify zero workout duration returns `400 Bad Request`
   - Verify non-existent user returns `404 Not Found`

---

## 17. Sample Request & Response Payloads

### Log Workout (`POST /api/workouts`)
**Request:**
```json
{
  "userId": 1,
  "workoutType": "RUNNING",
  "durationMinutes": 30,
  "caloriesBurned": 250,
  "workoutDate": "2026-09-28",
  "notes": "Morning run"
}
```
**Response (201 Created):**
```json
{
  "id": 5,
  "userId": 1,
  "userName": "Arun Kumar",
  "workoutType": "RUNNING",
  "durationMinutes": 30,
  "caloriesBurned": 250,
  "workoutDate": "2026-09-28",
  "notes": "Morning run",
  "createdAt": "2026-09-28T17:30:00"
}
```

### Daily Calorie Summary (`GET /api/users/1/summary/daily?date=2026-09-28`)
**Response (200 OK):**
```json
{
  "userId": 1,
  "date": "2026-09-28",
  "caloriesConsumed": 1800,
  "caloriesBurned": 600,
  "netCalories": 1200
}
```

### Negative Workout Calories Rejected (`POST /api/workouts`)
**Request:**
```json
{
  "userId": 1,
  "workoutType": "RUNNING",
  "durationMinutes": 30,
  "caloriesBurned": -100,
  "workoutDate": "2026-09-28"
}
```
**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-09-28T17:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Calories burned must be a non-negative value.",
  "path": "/api/workouts"
}
```

### Weekly Summary on Empty Week (`GET /api/users/1/summary/weekly?weekStart=2035-01-01`)
**Response (404 Not Found - Rule 2):**
```json
{
  "timestamp": "2026-09-28T17:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "No workout or meal entries found for the selected week.",
  "path": "/api/users/1/summary/weekly"
}
```

---

## 18. College Practical Viva Questions & Answers

1. **Why Spring Boot instead of standard Java Servlets?**
   - *Answer*: Spring Boot provides opinionated defaults, embedded Tomcat, automatic dependency management via Starters, and seamless dependency injection, eliminating thousands of lines of boilerplate XML and configuration.

2. **Why use DTOs instead of exposing JPA Entities directly?**
   - *Answer*: DTOs prevent over-posting vulnerabilities, decouple the internal database schema from the external API contract, avoid infinite recursion with bi-directional relationships during JSON serialization, and enable fine-grained bean validation.

3. **Where should business rules be placed and why?**
   - *Answer*: Business rules belong inside the **Service Layer**. Placing them exclusively in controllers causes duplication and makes unit testing impossible. Placing them solely in the database violates separation of concerns and leads to unhelpful generic SQL exception messages instead of clean HTTP responses.

4. **How are calories and net balance calculated?**
   - *Answer*: Net calories are dynamically computed in `SummaryService` by querying persisted `Meal` records for calories consumed and `Workout` records for calories burned on that date, then subtracting: `netCalories = caloriesConsumed - caloriesBurned`. They are not statically stored to avoid data inconsistency.

5. **Where and how is Mandatory Rule 2 enforced?**
   - *Answer*: In `SummaryService.getWeeklySummary()`. If the queries for workouts and meals both return empty lists for the requested 7-day interval, a `ResourceNotFoundException` is raised, which `GlobalExceptionHandler` converts to an `HTTP 404 NOT FOUND` with the descriptive message.

---

## 19. College Practical Demonstration Walkthrough

1. **Step 1**: Start MySQL Server (`sudo systemctl start mysql`).
2. **Step 2**: Start FitLog (`mvn spring-boot:run` or run in IntelliJ).
3. **Step 3**: Open browser at `http://localhost:8080`.
4. **Step 4**: Show users dropdown populated with **Arun Kumar**, **Priya Sharma**, and **Demo Student**.
5. **Step 5**: Log a new workout (e.g. GYM, 45 mins, 300 kcal).
6. **Step 6**: Log a new meal (e.g. Oatmeal, 300 kcal).
7. **Step 7**: Open the **Daily Calorie Balance** card to demonstrate:
   - Calories Consumed: 1800 kcal
   - Calories Burned: 600 kcal
   - Net Calories: 1200 kcal (Calorie Surplus badge).
8. **Step 8**: Navigate to **Weekly Summary & Trend** to show 7-day breakdown.
9. **Step 9**: Navigate to **Goal & Progress** to show weekly workout goal achievement meter.
10. **Step 10**: Navigate to **Viva / Business Rules Lab**:
    - Click **Test Rule 1 (Negative Calories)** -> Show instant HTTP 400 Bad Request response.
    - Click **Test Rule 2 (Empty Week Summary)** -> Show instant HTTP 404 Not Found response.
