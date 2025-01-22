package com.toulios.githubanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toulios.githubanalyzer.exception.RepoUpdateException;
import com.toulios.githubanalyzer.model.ObservedRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ObservedRepoHelper {

    private static final String LOG_PREFIX = "[ObservedRepoHelper] ";
    @Value("${app.kafka.topics.repo-changes}")
    private String repoChangesTopic;
    private final MessageService messageService;

    /**
     * Handles changes in a repository and sends a message to Kafka if there are any changes.
     *
     * @param oldValues the old repository values
     * @param newValues the new repository values
     */
    public void handleChanges(ObservedRepo oldValues, ObservedRepo newValues) {
        StringBuilder changes = new StringBuilder();

        if (!Objects.equals(oldValues.getName(), newValues.getName())) {
            changes.append("\n - Name: ").append(oldValues.getName()).append(" → ").append(newValues.getName());
        }
        if (!Objects.equals(oldValues.getOwner(), newValues.getOwner())) {
            changes.append("\n - Owner: ").append(oldValues.getOwner()).append(" → ").append(newValues.getOwner());
        }
        if (!Objects.equals(oldValues.getStars(), newValues.getStars())) {
            changes.append("\n - Stars: ").append(oldValues.getStars()).append(" → ").append(newValues.getStars());
        }
        if (!Objects.equals(oldValues.getOpenIssues(), newValues.getOpenIssues())) {
            changes.append("\n - Open Issues: ").append(oldValues.getOpenIssues()).append(" → ").append(newValues.getOpenIssues());
        }
        if (!Objects.equals(oldValues.getUrl(), newValues.getUrl())) {
            changes.append("\n - URL: ").append(oldValues.getUrl()).append(" → ").append(newValues.getUrl());
        }
        if (!Objects.equals(oldValues.getStatus(), newValues.getStatus())) {
            changes.append("\n - Status: ").append(oldValues.getStatus()).append(" → ").append(newValues.getStatus());
        }
        if (!Objects.equals(oldValues.getLicence(), newValues.getLicence())) {
            changes.append("\n - Licence: ").append(oldValues.getLicence()).append(" → ").append(newValues.getLicence());
        }

        if (changes.toString().trim().isEmpty()) {
            log.info("{} No changes detected for repository id: {}", LOG_PREFIX, newValues.getId());
            return;
        }

        try {
            changes.append("\nRepository changes for id").append(newValues.getId()).append(":");
            messageService.sendChangeEvent(repoChangesTopic, newValues.getId(), changes.toString());
        } catch (JsonProcessingException exception) {
            throw new RepoUpdateException(exception.getMessage());
        }
    }
}
