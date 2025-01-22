package com.toulios.githubanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toulios.githubanalyzer.dto.request.ObservedRepoFilter;
import com.toulios.githubanalyzer.dto.request.ObservedRepoRequest;
import com.toulios.githubanalyzer.dto.request.ObservedRepoUpdateRequest;
import com.toulios.githubanalyzer.dto.response.ObservedRepoResponse;
import com.toulios.githubanalyzer.dto.response.PaginatedResponse;
import com.toulios.githubanalyzer.exception.RepoCreationException;
import com.toulios.githubanalyzer.exception.RepoNotFoundException;
import com.toulios.githubanalyzer.model.ObservedRepo;
import com.toulios.githubanalyzer.model.ObservedRepoStatus;
import com.toulios.githubanalyzer.repository.ObservedRepoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservedRepoCrudServiceTest {

    @Mock
    private ObservedRepoRepository repository;

    @Mock
    private MessageService messageService;

    @Mock
    private ObservedRepoHelper observedRepoHelper;

    @InjectMocks
    private ObservedRepoCrudService service;

    private ObservedRepo testRepo;
    private ObservedRepoRequest testRequest;

    @BeforeEach
    void setUp() {
        testRepo = new ObservedRepo();
        testRepo.setId(1L);
        testRepo.setName("test-repo");
        testRepo.setOwner("test-owner");
        testRepo.setStars(100);
        testRepo.setOpenIssues(10);
        testRepo.setStatus(ObservedRepoStatus.ACTIVE);

        testRequest = new ObservedRepoRequest();
        testRequest.setName("test-repo");
        testRequest.setOwner("test-owner");
        testRequest.setStars(100);
        testRequest.setOpenIssues(10);
    }

    @Test
    void insert_WhenNewRepo_ShouldCreateAndReturnRepo() {
        when(repository.findByOwnerAndName(anyString(), anyString())).thenReturn(Optional.empty());
        when(repository.save(any(ObservedRepo.class))).thenReturn(testRepo);

        ObservedRepoResponse response = service.insert(testRequest);

        assertNotNull(response);
        assertEquals(testRepo.getName(), response.getName());
        assertEquals(testRepo.getOwner(), response.getOwner());
        verify(repository).save(any(ObservedRepo.class));
    }

    @Test
    void insert_WhenRepoExists_ShouldReturnExistingRepo() {
        when(repository.findByOwnerAndName(anyString(), anyString())).thenReturn(Optional.of(testRepo));

        ObservedRepoResponse response = service.insert(testRequest);

        assertNotNull(response);
        assertEquals(testRepo.getName(), response.getName());
        verify(repository, never()).save(any(ObservedRepo.class));
    }

    @Test
    void insert_WhenError_ShouldThrowRepoCreationException() {
        when(repository.findByOwnerAndName(anyString(), anyString())).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RepoCreationException.class, () -> service.insert(testRequest));
    }

    @Test
    void getById_WhenRepoExists_ShouldReturnRepo() {
        when(repository.findById(1L)).thenReturn(Optional.of(testRepo));

        ObservedRepoResponse response = service.getById(1L);

        assertNotNull(response);
        assertEquals(testRepo.getName(), response.getName());
    }

    @Test
    void getById_WhenRepoNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RepoNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void listAll_ShouldReturnPaginatedResponse() {
        // Create a filter with non-null values
        ObservedRepoFilter filter = ObservedRepoFilter
                .builder()
                .build();
        filter.setStatus(ObservedRepoStatus.ACTIVE);  // Set a non-null status
        filter.setOwner("test-owner");  // Optionally set other filter fields

        Page<ObservedRepo> page = new PageImpl<>(Collections.singletonList(testRepo));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);

        PaginatedResponse<ObservedRepoResponse> response = service.listAll(filter, pageable);

        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    // Add a new test for null filter values
    @Test
    void listAll_WithNullFilterValues_ShouldReturnPaginatedResponse() {
        // Create a filter with null values
        ObservedRepoFilter filter = ObservedRepoFilter.builder().build();
        // Don't set any filter values - leave them null

        Page<ObservedRepo> page = new PageImpl<>(Collections.singletonList(testRepo));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);

        PaginatedResponse<ObservedRepoResponse> response = service.listAll(filter, pageable);

        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void deleteById_WhenRepoExists_ShouldMarkAsDeleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(testRepo));
        when(repository.save(any(ObservedRepo.class))).thenReturn(testRepo);

        service.deleteById(1L);

        verify(repository).save(argThat(repo ->
                repo.getStatus() == ObservedRepoStatus.DELETED
        ));
    }

    @Test
    void update_WhenRepoExists_ShouldUpdateAndNotifyChanges() throws JsonProcessingException {
        when(repository.findById(1L)).thenReturn(Optional.of(testRepo));
        when(repository.save(any(ObservedRepo.class))).thenReturn(testRepo);

        ObservedRepoUpdateRequest updateRequest = new ObservedRepoUpdateRequest();
        updateRequest.setStars(JsonNullable.of(200));
        updateRequest.setOpenIssues(JsonNullable.of(20));

        ObservedRepoResponse response = service.update(1L, updateRequest);

        assertNotNull(response);
        verify(repository).save(any(ObservedRepo.class));
        verify(observedRepoHelper).handleChanges(any(), any());
    }

    @Test
    void update_WhenRepoNotFound_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ObservedRepoUpdateRequest updateRequest = new ObservedRepoUpdateRequest();
        updateRequest.setStars(JsonNullable.of(200));

        assertThrows(RepoNotFoundException.class, () -> service.update(1L, updateRequest));
    }
} 