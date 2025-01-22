package com.toulios.githubanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for GitHub Repository Analyzer
 */
@SpringBootApplication
@EnableScheduling
public class GithubAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GithubAnalyzerApplication.class, args);
    }
} 