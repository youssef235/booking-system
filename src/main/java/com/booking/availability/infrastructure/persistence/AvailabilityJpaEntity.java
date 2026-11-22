package com.booking.availability.infrastructure.persistence;

import com.booking.availability.domain.AvailabilityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "availabilities")
class AvailabilityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AvailabilityStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AvailabilityJpaEntity() {
    }

    AvailabilityJpaEntity(Long id, Long providerId, Instant startTime, Instant endTime,
                          AvailabilityStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    Long getId() { return id; }

    Long getProviderId() { return providerId; }

    Instant getStartTime() { return startTime; }

    Instant getEndTime() { return endTime; }

    AvailabilityStatus getStatus() { return status; }

    Instant getCreatedAt() { return createdAt; }

    Instant getUpdatedAt() { return updatedAt; }
}