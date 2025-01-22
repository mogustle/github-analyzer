package com.toulios.githubanalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity class representing a GitHub repository
 */
@Entity
@Table(name = "observed_repo")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ObservedRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String url;

    @Column(name = "repo_owner", length = 1000)
    private String owner;

    @Column(name = "repo_name", length = 1000)
    private String name;

    private Integer stars;

    private Integer openIssues;

    @Column(length = 100)
    private String licence;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "repo_status", nullable = false)
    private ObservedRepoStatus status = ObservedRepoStatus.ACTIVE;
}