package com.booking.service.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class ServiceOffering {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int PRICE_SCALE = 2;
    private static final Duration MAX_DURATION = Duration.ofHours(8);

    private final Long id;
    private final Long providerId;
    private String name;
    private String description;
    private BigDecimal price;
    private Duration duration;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private ServiceOffering(Long id, Long providerId, String name, String description, BigDecimal price,
                            Duration duration, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ServiceOffering create(Long providerId, String name, String description, BigDecimal price,
                                         Duration duration, Instant now) {
        return new ServiceOffering(null, requireProviderId(providerId), requireName(name),
                normalizeDescription(description), requirePrice(price), requireDuration(duration),
                true, now, now);
    }

    public static ServiceOffering restore(Long id, Long providerId, String name, String description,
                                          BigDecimal price, Duration duration, boolean active,
                                          Instant createdAt, Instant updatedAt) {
        return new ServiceOffering(id, providerId, name, description, price, duration, active,
                createdAt, updatedAt);
    }

    public void update(String newName, String newDescription, BigDecimal newPrice, Duration newDuration,
                       Instant now) {
        this.name = requireName(newName);
        this.description = normalizeDescription(newDescription);
        this.price = requirePrice(newPrice);
        this.duration = requireDuration(newDuration);
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        this.active = true;
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    public boolean belongsTo(Long candidateProviderId) {
        return providerId.equals(candidateProviderId);
    }

    public Long getId() {
        return id;
    }

    public Long getProviderId() {
        return providerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Duration getDuration() {
        return duration;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static Long requireProviderId(Long providerId) {
        if (providerId == null) {
            throw new InvalidServiceException("Service must belong to a provider");
        }
        return providerId;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidServiceException("Service name must not be blank");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new InvalidServiceException("Service name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
        return trimmed;
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        String trimmed = description.trim();
        if (trimmed.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidServiceException(
                    "Service description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        return trimmed;
    }

    private static BigDecimal requirePrice(BigDecimal price) {
        if (price == null) {
            throw new InvalidServiceException("Price is required");
        }
        if (price.signum() < 0) {
            throw new InvalidServiceException("Price must not be negative");
        }
        if (price.stripTrailingZeros().scale() > PRICE_SCALE) {
            throw new InvalidServiceException("Price must have at most " + PRICE_SCALE + " decimal places");
        }
        return price.setScale(PRICE_SCALE);
    }

    private static Duration requireDuration(Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new InvalidServiceException("Duration must be positive");
        }
        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new InvalidServiceException("Duration must not exceed " + MAX_DURATION.toHours() + " hours");
        }
        return duration;
    }
}
