package com.toulios.githubanalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the application.
 */
@Configuration
public class AppConfig {
    /**
     * Bean for the RestTemplate.
     *
     * @return the RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
