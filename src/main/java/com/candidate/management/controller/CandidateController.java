
package com.candidate.management.controller;

import com.candidate.management.dto.CandidateRequest;
import com.candidate.management.dto.CandidateResponse;
import com.candidate.management.model.Candidate;
import com.candidate.management.service.CandidateService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates")
@Validated
public class CandidateController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate create(@Valid @RequestBody CandidateRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public Candidate getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @GetMapping("/search")
    public CandidateResponse search(

            @RequestParam(name = "keyword", required = false) String keyword,

            @RequestParam(name = "position", required = false) String position,

            @RequestParam(name = "location", required = false) String location,

            @RequestParam(name = "minExperience", required = false)
            @Min(0) @Max(60) Integer minExperience,

            @RequestParam(name = "maxExperience", required = false)
            @Min(0) @Max(60) Integer maxExperience,

            @RequestParam(name = "page", defaultValue = "0")
            @Min(0) int page,

            @RequestParam(name = "size", defaultValue = "20")
            @Min(1) @Max(100) int size,

            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,

            @RequestParam(name = "direction", defaultValue = "asc") String direction
    ) {

        if (minExperience != null && maxExperience != null
                && minExperience > maxExperience) {

            throw new IllegalArgumentException(
                    "minExperience must be less than or equal to maxExperience");
        }

        if (!java.util.Set.of(
                "name", "email", "experienceYears", "createdAt"
        ).contains(sortBy)) {

            throw new IllegalArgumentException("Unsupported sortBy field");
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return service.search(
                keyword,
                position,
                location,
                minExperience,
                maxExperience,
                PageRequest.of(page, size, sort)
        );
    }

    @PutMapping("/update/{id}")
    public Candidate update(
            @PathVariable("id") Long id,
            @Valid @RequestBody CandidateRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
