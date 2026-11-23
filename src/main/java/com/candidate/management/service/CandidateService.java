package com.candidate.management.service;

import com.candidate.management.dto.CandidateRequest;
import com.candidate.management.dto.CandidateResponse;
import com.candidate.management.dto.CandidateResponse;
import com.candidate.management.exception.CandidateNotFoundException;
import com.candidate.management.exception.DuplicateEmailException;
import com.candidate.management.model.Candidate;
import com.candidate.management.repository.CandidateRepository;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CandidateService {

    private static final Logger log =
            LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository repository;

    public CandidateService(CandidateRepository repository) {
        this.repository = repository;
    }

    public Candidate create(CandidateRequest request) {

        log.info("Creating candidate with email={}", request.email());

        if (repository.existsByEmailIgnoreCase(request.email())) {
            log.warn("Candidate creation rejected: email already exists");
            throw new DuplicateEmailException(request.email());
        }

        Candidate candidate = new Candidate();
        apply(candidate, request);

        Candidate saved = repository.save(candidate);

        log.info("Candidate created successfully with id={}", saved.getId());

        return saved;
    }

    @Transactional(readOnly = true)
    public Candidate getById(Long id) {

        log.debug("Fetching candidate with id={}", id);

        return repository.findById(id).orElseThrow(() -> {
            log.warn("Candidate not found with id={}", id);
            return new CandidateNotFoundException(id);
        });
    }

    @Transactional(readOnly = true)
    public CandidateResponse search(
            String keyword,
            String position,
            String location,
            Integer minExperience,
            Integer maxExperience,
            Pageable pageable) {

        log.debug(
                "Searching candidates with filters: keyword={}, position={}, location={}, minExperience={}, maxExperience={}",
                keyword, position, location, minExperience, maxExperience
        );

        log.debug(
                "Applying pagination: page={}, size={}, sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );

        Specification<Candidate> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {

                String pattern = "%" + keyword.trim().toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("skills")), pattern),
                        cb.like(cb.lower(root.get("position")), pattern)
                ));
            }

            if (StringUtils.hasText(position)) {
                predicates.add(cb.like(
                        cb.lower(root.get("position")),
                        "%" + position.trim().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(location)) {
                predicates.add(cb.like(
                        cb.lower(root.get("location")),
                        "%" + location.trim().toLowerCase() + "%"
                ));
            }

            if (minExperience != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("experienceYears"), minExperience
                ));
            }

            if (maxExperience != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("experienceYears"), maxExperience
                ));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Page<Candidate> results = repository.findAll(spec, pageable);

        log.debug(
                "Candidate search completed: {} results found on page {}",
                results.getNumberOfElements(),
                results.getNumber()
        );

        return new CandidateResponse(
                results.getContent(),
                results.getTotalElements(),
                results.getTotalPages(),
                results.getSize(),
                results.getNumber()
        );
    }

    public Candidate update(Long id, CandidateRequest request) {

        log.info("Updating candidate with id={}", id);

        Candidate candidate = getById(id);

        if (repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            log.warn("Candidate update rejected: email already exists for another candidate");
            throw new DuplicateEmailException(request.email());
        }

        apply(candidate, request);

        Candidate updated = repository.save(candidate);

        log.info("Candidate updated successfully with id={}", updated.getId());

        return updated;
    }

    public void delete(Long id) {

        log.info("Deleting candidate with id={}", id);

        Candidate candidate = getById(id);
        repository.delete(candidate);

        log.info("Candidate deleted successfully with id={}", id);
    }

    private void apply(Candidate c, CandidateRequest r) {

        log.debug("Applying candidate request data");

        c.setName(r.name().trim());
        c.setEmail(r.email().trim().toLowerCase());
        c.setPhone(r.phone());
        c.setPosition(r.position());
        c.setSkills(r.skills());
        c.setExperienceYears(r.experienceYears());
        c.setLocation(r.location());
    }
}