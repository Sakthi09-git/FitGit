package com.fitlog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger documentation configuration for FitLog backend.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fitLogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitLog — Personal Workout and Calorie Tracker API")
                        .description("REST API documentation for FitLog personal workout and calorie tracking backend application. " +
                                "Supports daily workouts, meal logs, daily calorie balances, weekly trends, and goal tracking.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FitLog Development Team")
                                .email("support@fitlog.local"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
