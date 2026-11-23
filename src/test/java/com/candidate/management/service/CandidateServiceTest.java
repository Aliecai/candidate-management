package com.candidate.management.service;

import com.candidate.management.dto.CandidateRequest;
import com.candidate.management.dto.CandidateResponse;
import com.candidate.management.dto.CandidateResponse;
import com.candidate.management.exception.CandidateNotFoundException;
import com.candidate.management.exception.DuplicateEmailException;
import com.candidate.management.model.Candidate;
import com.candidate.management.repository.CandidateRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    CandidateRepository repository;

    @InjectMocks
    CandidateService service;

    private CandidateRequest request(String email) {
        return new CandidateRequest(
                "Alex Doe",
                email,
                "1234567890",
                "Java Engineer",
                "Java, Spring",
                4,
                "Bengaluru"
        );
    }

    @Test
    void createsCandidateAndNormalizesEmail() {

        when(repository.existsByEmailIgnoreCase("ALEX@EXAMPLE.COM"))
                .thenReturn(false);

        when(repository.save(any(Candidate.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Candidate created = service.create(request("ALEX@EXAMPLE.COM"));

        assertEquals("alex@example.com", created.getEmail());
        assertEquals("Alex Doe", created.getName());

        verify(repository).save(any(Candidate.class));
    }

    @Test
    void rejectsDuplicateEmail() {

        when(repository.existsByEmailIgnoreCase("alex@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> service.create(request("alex@example.com"))
        );

        verify(repository, never()).save(any(Candidate.class));
    }

    @Test
    void returnsNotFoundForUnknownId() {

        when(repository.findById(42L))
                .thenReturn(Optional.empty());

        assertThrows(
                CandidateNotFoundException.class,
                () -> service.getById(42L)
        );
    }

    @Test
    void searchesCandidatesAndReturnsCustomPaginationResponse() {

        Candidate candidate = new Candidate();
        candidate.setName("Alex Doe");
        candidate.setEmail("alex@example.com");
        candidate.setExperienceYears(4);
        candidate.setLocation("Bengaluru");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Candidate> page = new PageImpl<>(
                List.of(candidate),
                pageable,
                1
        );

        when(repository.findAll(
                org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<Candidate>>any(),
                eq(pageable)
        )).thenReturn(page);

        CandidateResponse response = service.search(
                null, null, null, null, null, pageable
        );

        assertEquals(1, response.content().size());
        assertEquals(1, response.totalElements());
        assertEquals(1, response.totalPages());
        assertEquals(10, response.size());
        assertEquals(0, response.number());

        assertEquals("Alex Doe", response.content().get(0).getName());

        verify(repository).findAll(
                org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<Candidate>>any(),
                eq(pageable)
        );
    }
}