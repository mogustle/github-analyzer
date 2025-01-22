package com.toulios.githubanalyzer.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rate limiter for the GitHub API.
 * This class is responsible for rate limiting requests to the GitHub API.
 * It uses a buffer to avoid exceeding the rate limit.
 */
@Slf4j
@Component
public class GithubRateLimiter {
    private final static String LOG_PREFIX = "[GithubRateLimiter]";
    private final AtomicInteger remainingRequests;
    private final ReentrantLock lock;
    private final GithubProperties properties;
    private volatile int rateLimit;
    private volatile Instant resetTime;
    private final int bufferSize;

    /**
     * Constructor for the GithubRateLimiter.
     * @param properties the properties for the rate limiter
     */
    public GithubRateLimiter(GithubProperties properties) {
        this.properties = properties;
        this.rateLimit = properties.getDefaultLimit();
        this.remainingRequests = new AtomicInteger(properties.getDefaultLimit());
        this.resetTime = Instant.now().plusSeconds(properties.getDefaultWindowSeconds());
        this.lock = new ReentrantLock();
        this.bufferSize = calculateBufferSize();
    }

    /**
     * Calculates the buffer size based on the default limit and buffer percentage.
     * @return the buffer size
     */
    private int calculateBufferSize() {
        return (int) Math.ceil(properties.getDefaultLimit() * (properties.getBufferPercentage() / 100.0));
    }

    /**
     * Waits if necessary to avoid exceeding the rate limit.
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void waitIfNeeded() throws InterruptedException {
        lock.lock();
        try {
            // Consider the buffer when checking remaining requests
            if (remainingRequests.get() <= bufferSize) {
                long waitTimeMillis = Instant.now().until(resetTime, java.time.temporal.ChronoUnit.MILLIS);
                if (waitTimeMillis > 0) {
                    log.warn("{} Rate limit reaching buffer zone ({} requests remaining, buffer size: {}). " +
                            "Waiting for {} seconds until reset", 
                            LOG_PREFIX, remainingRequests.get(), bufferSize, waitTimeMillis / 1000);
                    Thread.sleep(waitTimeMillis);
                }
                // After waiting (or if reset time has passed), restore the rate limit
                remainingRequests.set(rateLimit);
                resetTime = Instant.now().plusSeconds(properties.getDefaultWindowSeconds());
            }
            remainingRequests.decrementAndGet();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Updates the rate limits based on the response from the GitHub API.
     * @param remaining the remaining requests
     * @param limit the rate limit
     * @param newResetTime the new reset time
     */
    public void updateRateLimits(int remaining, int limit, Instant newResetTime) {
        lock.lock();
        try {
            this.remainingRequests.set(remaining);
            this.rateLimit = limit;
            this.resetTime = newResetTime;
            
            // Log if we're approaching the buffer zone
            if (remaining <= (bufferSize * 2)) {
                log.warn("{} Approaching rate limit buffer zone. Remaining: {}, Buffer: {}", 
                        LOG_PREFIX, remaining, bufferSize);
            }
        } finally {
            lock.unlock();
        }
    }
} 