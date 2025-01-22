package com.toulios.githubanalyzer.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for API versioning.
 */
@Configuration
public class ApiVersionConfig {
    public static final String API_VERSION_1 = "v1";
    public static final String BASE_API_PATH = "/api/" + API_VERSION_1;
} 