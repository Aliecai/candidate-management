package com.candidate.management.dto;

import jakarta.validation.constraints.*;

public record CandidateRequest(
    @NotBlank @Size(max = 120)
    String name,
    @NotBlank @Email @Size(max = 180)
    String email,
    @Size(max = 80)
    String phone,
    @Size(max = 120)
    String position,
    @Size(max = 120)
    String skills,
    @Min(0) @Max(60)
    Integer experienceYears,
    @Size(max = 80)
    String location
) {}
