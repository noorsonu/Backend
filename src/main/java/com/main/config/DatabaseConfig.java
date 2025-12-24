package com.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // Force Render PostgreSQL connection
        String jdbcUrl = "jdbc:postgresql://dpg-d55rr2muk2gs73c4ldng-a.oregon-postgres.render.com/yaallah?sslmode=require";
        String username = "yaallah_user";
        String password = "3M9FsFAAzniMEiior5PyHt6zvGaVxxtq";
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1200000);
        
        System.out.println("Using database URL: " + jdbcUrl);
        return new HikariDataSource(config);
    }
}