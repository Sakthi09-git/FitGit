package com.fitlog.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Intelligent DataSource configuration that automatically connects to MySQL
 * when running, and gracefully falls back to an embedded database (MySQL mode)
 * if the MySQL daemon is not running on localhost:3306.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username:root}")
    private String mysqlUsername;

    @Value("${spring.datasource.password:}")
    private String mysqlPassword;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String mysqlDriverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (isMySqlReachable()) {
            log.info("MySQL service detected on localhost:3306. Connecting to MySQL: {}", mysqlUrl);
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(mysqlUrl);
            ds.setUsername(mysqlUsername);
            ds.setPassword(mysqlPassword);
            ds.setDriverClassName(mysqlDriverClassName);
            ds.setPoolName("FitLog-MySQL-Pool");
            return ds;
        } else {
            log.warn("==========================================================================================");
            log.warn("Notice: MySQL daemon not detected on localhost:3306.");
            log.warn("Engaging automatic fallback: Embedded database in MySQL compatibility mode.");
            log.warn("Application will start immediately on http://localhost:8080 without errors.");
            log.warn("To use real MySQL: Start MySQL service ('sudo systemctl start mysql') and restart FitLog.");
            log.warn("==========================================================================================");

            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:h2:mem:fitlog_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
            ds.setDriverClassName("org.h2.Driver");
            ds.setUsername("sa");
            ds.setPassword("");
            ds.setPoolName("FitLog-Fallback-H2-Pool");
            return ds;
        }
    }

    private boolean isMySqlReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 3306), 400);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
