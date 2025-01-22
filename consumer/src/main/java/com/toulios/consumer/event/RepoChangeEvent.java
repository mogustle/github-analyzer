package com.toulios.consumer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event class representing a repository change.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepoChangeEvent {
    private Long repoId;
    private String changes;
    private LocalDateTime timestamp;
} 