package com.toulios.githubanalyzer.service;

import com.toulios.githubanalyzer.client.GithubApiClient;
import com.toulios.githubanalyzer.dto.GithubRepositoryDto;
import com.toulios.githubanalyzer.dto.OwnerDto;
import com.toulios.githubanalyzer.model.ObservedRepo;
import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import com.toulios.githubanalyzer.repository.ObservedRepoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ObservedRepoProcessingServiceTest {

    @Mock
    private GithubApiClient githubApiClient;

    @Mock
    private ObservedRepoRepository observedRepoRepository;

    @InjectMocks
    private ObservedRepoProcessingService service;

    @Captor
    private ArgumentCaptor<List<ObservedRepo>> reposCaptor;

    private ObservedRepo testRepo1;
    private ObservedRepo testRepo2;
    private GithubRepositoryDto githubRepo1;
    private GithubRepositoryDto githubRepo2;

    private OwnerDto ownerDto1;
    private OwnerDto ownerDto2;
    @BeforeEach
    void setUp() {
        testRepo1 = new ObservedRepo();
        testRepo1.setId(1L);
        testRepo1.setOwner("owner1");
        testRepo1.setName("repo1");
        testRepo1.setStatus(ObservedRepoStatus.ACTIVE);

        testRepo2 = new ObservedRepo();
        testRepo2.setId(2L);
        testRepo2.setOwner("owner2");
        testRepo2.setName("repo2");
        testRepo2.setStatus(ObservedRepoStatus.ACTIVE);

        ownerDto1 = new OwnerDto();
        ownerDto1.setLogin("owner1");
        githubRepo1 = new GithubRepositoryDto();
        githubRepo1.setOwner(ownerDto1);
        githubRepo1.setName("repo1");
        githubRepo1.setStars(100);

        ownerDto2 = new OwnerDto();
        ownerDto2.setLogin("owner2");
        githubRepo2 = new GithubRepositoryDto();
        githubRepo2.setOwner(ownerDto2);
        githubRepo2.setName("repo2");
        githubRepo2.setStars(200);
    }

    @Test
    void processObservedRepos_WithEmptyDatabase_ShouldLogAndReturn() {
        // Arrange
        Page<ObservedRepo> emptyPage = new PageImpl<>(Collections.emptyList());
        when(observedRepoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act
        service.processObservedRepos();

        // Assert
        verify(observedRepoRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(githubApiClient, never()).getRepositoryDetails(anyString(), anyString());
    }

    @Test
    void processObservedRepos_WithValidRepositories_ShouldProcessAllRepos() {
        // Arrange
        List<ObservedRepo> repos = Arrays.asList(testRepo1, testRepo2);
        Page<ObservedRepo> page = new PageImpl<>(repos);
        when(observedRepoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page)
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        
        when(githubApiClient.getRepositoryDetails("owner1", "repo1")).thenReturn(githubRepo1);
        when(githubApiClient.getRepositoryDetails("owner2", "repo2")).thenReturn(githubRepo2);

        // Act
        service.processObservedRepos();

        // Assert
        verify(observedRepoRepository, times(2)).findAll(any(Specification.class), any(Pageable.class));
        verify(githubApiClient).getRepositoryDetails("owner1", "repo1");
        verify(githubApiClient).getRepositoryDetails("owner2", "repo2");
        verify(observedRepoRepository).saveAll(reposCaptor.capture());
        
        List<ObservedRepo> savedRepos = reposCaptor.getValue();
        assertEquals(2, savedRepos.size());
        assertEquals(ObservedRepoStatus.ACTIVE, savedRepos.get(0).getStatus());
        assertEquals(ObservedRepoStatus.ACTIVE, savedRepos.get(1).getStatus());
    }

    @Test
    void processObservedRepos_WithInvalidRepository_ShouldMarkAsInvalid() {
        // Arrange
        Page<ObservedRepo> page = new PageImpl<>(Collections.singletonList(testRepo1));
        when(observedRepoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page)
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        
        when(githubApiClient.getRepositoryDetails("owner1", "repo1")).thenReturn(null);

        // Act
        service.processObservedRepos();

        // Assert
        verify(observedRepoRepository).saveAll(reposCaptor.capture());
        List<ObservedRepo> savedRepos = reposCaptor.getValue();
        assertEquals(1, savedRepos.size());
        assertEquals(ObservedRepoStatus.INVALID, savedRepos.get(0).getStatus());
    }

    @Test
    void processObservedRepos_WhenGithubApiThrowsException_ShouldHandleError() {
        // Arrange
        Page<ObservedRepo> page = new PageImpl<>(Collections.singletonList(testRepo1));
        when(observedRepoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page)
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        
        when(githubApiClient.getRepositoryDetails("owner1", "repo1"))
            .thenThrow(new RuntimeException("API Error"));

        // Act
        service.processObservedRepos();

        // Assert
        verify(observedRepoRepository).save(any(ObservedRepo.class));
        ArgumentCaptor<ObservedRepo> repoCaptor = ArgumentCaptor.forClass(ObservedRepo.class);
        verify(observedRepoRepository).save(repoCaptor.capture());
        assertEquals(ObservedRepoStatus.INVALID, repoCaptor.getValue().getStatus());
    }
} 