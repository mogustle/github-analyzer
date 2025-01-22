package com.toulios.githubanalyzer.repository;

import com.toulios.githubanalyzer.model.ObservedRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PublicRepoRepository entity
 */
@Repository
public interface ObservedRepoRepository extends JpaRepository<ObservedRepo, Long>, JpaSpecificationExecutor<ObservedRepo> {
    Optional<ObservedRepo> findByOwnerAndName(String owner, String name);
} 