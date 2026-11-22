package com.booking.provider.domain;

import java.time.Instant;

public class Provider {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    private final Long id;
    private final Long userId;
    private String name;
    private String description;
    private ProviderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Provider(Long id, Long userId, String name, String description, ProviderStatus status,
                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Provider register(Long userId, String name, String description, Instant now) {
        return new Provider(null, requireUserId(userId), requireName(name), normalizeDescription(description),
                ProviderStatus.ACTIVE, now, now);
    }

    public static Provider restore(Long id, Long userId, String name, String description, ProviderStatus status,
                                   Instant createdAt, Instant updatedAt) {
        return new Provider(id, userId, name, description, status, createdAt, updatedAt);
    }

    public void updateDetails(String newName, String newDescription, Instant now) {
        this.name = requireName(newName);
        this.description = normalizeDescription(newDescription);
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        changeStatus(ProviderStatus.ACTIVE, now);
    }

    public void deactivate(Instant now) {
        changeStatus(ProviderStatus.INACTIVE, now);
    }

    public void suspend(Instant now) {
        changeStatus(ProviderStatus.SUSPENDED, now);
    }

    public boolean acceptsBookings() {
        return status.acceptsBookings();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProviderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void changeStatus(ProviderStatus newStatus, Instant now) {
        this.status = newStatus;
        this.updatedAt = now;
    }

    private static Long requireUserId(Long userId) {
        if (userId == null) {
            throw new InvalidProviderException("Provider must be linked to a user");
        }
        return userId;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidProviderException("Provider name must not be blank");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new InvalidProviderException("Provider name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
        return trimmed;
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        String trimmed = description.trim();
        if (trimmed.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidProviderException(
                    "Provider description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        return trimmed;
    }
}
