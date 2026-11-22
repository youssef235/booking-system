package com.booking.user.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.regex.Pattern;

public class User {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MAX_NAME_LENGTH = 100;

    private final Long id;
    private String name;
    private final String email;
    private final String passwordHash;
    private UserRole role;
    private boolean enabled;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(Long id, String name, String email, String passwordHash, UserRole role,
                 boolean enabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User register(String name, String email, String passwordHash, UserRole role, Instant now) {
        return new User(null, requireName(name), requireEmail(email), requirePasswordHash(passwordHash),
                requireRole(role), true, now, now);
    }

    public static User restore(Long id, String name, String email, String passwordHash, UserRole role,
                               boolean enabled, Instant createdAt, Instant updatedAt) {
        return new User(id, name, email, passwordHash, role, enabled, createdAt, updatedAt);
    }

    public void rename(String newName, Instant now) {
        this.name = requireName(newName);
        this.updatedAt = now;
    }

    public void disable(Instant now) {
        this.enabled = false;
        this.updatedAt = now;
    }

    public void enable(Instant now) {
        this.enabled = true;
        this.updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidUserException("Name must not be blank");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new InvalidUserException("Name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
        return trimmed;
    }

    private static String requireEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidUserException("Email must not be blank");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidUserException("Email format is invalid");
        }
        return normalized;
    }

    private static String requirePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new InvalidUserException("Password hash must not be blank");
        }
        return passwordHash;
    }

    private static UserRole requireRole(UserRole role) {
        if (role == null) {
            throw new InvalidUserException("Role is required");
        }
        return role;
    }
}
