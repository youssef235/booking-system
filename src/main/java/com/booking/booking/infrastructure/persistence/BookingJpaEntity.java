package com.booking.booking.infrastructure.persistence;

import com.booking.booking.domain.BookingStatus;
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
@Table(name = "bookings")
class BookingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BookingJpaEntity() {
    }

    BookingJpaEntity(Long id, Long userId, Long serviceId, Long providerId, Instant startTime, Instant endTime,
                     BookingStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.providerId = providerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    Long getId() { return id; }

    Long getUserId() { return userId; }

    Long getServiceId() { return serviceId; }

    Long getProviderId() { return providerId; }

    Instant getStartTime() { return startTime; }

    Instant getEndTime() { return endTime; }

    BookingStatus getStatus() { return status; }

    Instant getCreatedAt() { return createdAt; }

    Instant getUpdatedAt() { return updatedAt; }
}