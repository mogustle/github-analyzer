package com.toulios.githubanalyzer.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Flyway.
 */
@Configuration
public class FlywayConfig {
    
    /**
     * Bean for the Flyway migration strategy.
     * @return the Flyway migration strategy
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        // You can add custom migration logic here if needed
        return Flyway::migrate;
    }
} 