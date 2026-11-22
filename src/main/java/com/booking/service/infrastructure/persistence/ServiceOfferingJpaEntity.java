package com.booking.service.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "services")
class ServiceOfferingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_seconds", nullable = false)
    private long durationSeconds;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ServiceOfferingJpaEntity() {
    }

    ServiceOfferingJpaEntity(Long id, Long providerId, String name, String description, BigDecimal price,
                             long durationSeconds, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationSeconds = durationSeconds;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    Long getId() { return id; }

    Long getProviderId() { return providerId; }

    String getName() { return name; }

    String getDescription() { return description; }

    BigDecimal getPrice() { return price; }

    long getDurationSeconds() { return durationSeconds; }

    boolean isActive() { return active; }

    Instant getCreatedAt() { return createdAt; }

    Instant getUpdatedAt() { return updatedAt; }
}