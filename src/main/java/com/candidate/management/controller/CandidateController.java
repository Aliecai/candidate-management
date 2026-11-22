package com.candidate.management.controller;

import com.candidate.management.dto.CandidateRequest;
import com.candidate.management.model.Candidate;
import com.candidate.management.service.CandidateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates")
@Validated
public class CandidateController {
    private final CandidateService service;
    public CandidateController(CandidateService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate create(@Valid @RequestBody CandidateRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public Candidate getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<Candidate> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String position,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) @Min(0) @Max(60) Integer minExperience,
        @RequestParam(required = false) @Min(0) @Max(60) Integer maxExperience,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String direction
    ) {
        if (minExperience != null && maxExperience != null && minExperience > maxExperience)
            throw new IllegalArgumentException("minExperience must be less than or equal to maxExperience");

        if (!java.util.Set.of("name", "email", "experienceYears", "createdAt").contains(sortBy))
            throw new IllegalArgumentException("Unsupported sortBy field");

        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return service.search(keyword, position, location, minExperience, maxExperience,
            PageRequest.of(page, size, sort));
    }

    @PutMapping("/{id}")
    public Candidate update(@PathVariable Long id, @Valid @RequestBody CandidateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
