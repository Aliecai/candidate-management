package com.candidate.management.dto;

import com.candidate.management.model.Candidate;
import java.util.List;

public record CandidateResponse(
        List<Candidate> content,
        long totalElements,
        int totalPages,
        int size,
        int number
) {}