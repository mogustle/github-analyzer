package com.toulios.githubanalyzer.util;

import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Utility class for working with JsonNullable objects.
 */
public class JsonNullableUtils {
    /**
     * Updates a property if the JsonNullable value is present.
     * @param nullable the JsonNullable value to check
     * @param setter the setter to call if the value is present
     * @param <T> the type of the value
     */
    public static <T> void updateIfPresent(JsonNullable<T> nullable, PropertySetter<T> setter) {
        if (nullable != null && nullable.isPresent()) {
            setter.set(nullable.get());
        }
    }

    /**
     * Functional interface for setting a property.
     * @param <T> the type of the value
     */
    @FunctionalInterface
    public interface PropertySetter<T> {
        void set(T value);
    }
} 