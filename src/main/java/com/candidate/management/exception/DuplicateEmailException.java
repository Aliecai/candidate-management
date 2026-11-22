package com.candidate.management.exception;
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("A candidate with email already exists: " + email);
    }
}
