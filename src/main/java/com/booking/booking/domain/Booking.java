package com.booking.booking.domain;

import java.time.Instant;

public class Booking {

    private final Long id;
    private final Long userId;
    private final Long serviceId;
    private final Long providerId;
    private Instant startTime;
    private Instant endTime;
    private BookingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Booking(Long id, Long userId, Long serviceId, Long providerId, Instant startTime, Instant endTime,
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

    public static Booking create(Long userId, Long serviceId, Long providerId, Instant startTime, Instant endTime,
                                 Instant now) {
        requireId(userId, "User is required");
        requireId(serviceId, "Service is required");
        requireId(providerId, "Provider is required");
        requireWindow(startTime, endTime, now);
        return new Booking(null, userId, serviceId, providerId, startTime, endTime,
                BookingStatus.PENDING, now, now);
    }

    public static Booking restore(Long id, Long userId, Long serviceId, Long providerId, Instant startTime,
                                  Instant endTime, BookingStatus status, Instant createdAt, Instant updatedAt) {
        return new Booking(id, userId, serviceId, providerId, startTime, endTime, status, createdAt, updatedAt);
    }

    public void confirm(Instant now) {
        changeStatus(BookingStatus.CONFIRMED, now);
    }

    public void reject(Instant now) {
        changeStatus(BookingStatus.REJECTED, now);
    }

    public void cancel(Instant now) {
        requireTransition(BookingStatus.CANCELLED);
        if (!now.isBefore(startTime)) {
            throw new InvalidBookingException("Booking cannot be cancelled after it has started");
        }
        apply(BookingStatus.CANCELLED, now);
    }

    public void complete(Instant now) {
        requireTransition(BookingStatus.COMPLETED);
        if (now.isBefore(endTime)) {
            throw new InvalidBookingException("Booking cannot be completed before it ends");
        }
        apply(BookingStatus.COMPLETED, now);
    }

    public void reschedule(Instant newStartTime, Instant newEndTime, Instant now) {
        if (status != BookingStatus.PENDING) {
            throw new InvalidBookingException("Only pending bookings can be rescheduled");
        }
        requireWindow(newStartTime, newEndTime, now);
        this.startTime = newStartTime;
        this.endTime = newEndTime;
        this.updatedAt = now;
    }

    public boolean isOwnedBy(Long candidateUserId) {
        return userId.equals(candidateUserId);
    }

    public boolean belongsToProvider(Long candidateProviderId) {
        return providerId.equals(candidateProviderId);
    }

    public boolean overlaps(Instant otherStart, Instant otherEnd) {
        return startTime.isBefore(otherEnd) && otherStart.isBefore(endTime);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void changeStatus(BookingStatus target, Instant now) {
        requireTransition(target);
        apply(target, now);
    }

    private void requireTransition(BookingStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidBookingTransitionException(status, target);
        }
    }

    private void apply(BookingStatus target, Instant now) {
        this.status = target;
        this.updatedAt = now;
    }

    private static void requireId(Long value, String message) {
        if (value == null) {
            throw new InvalidBookingException(message);
        }
    }

    private static void requireWindow(Instant startTime, Instant endTime, Instant now) {
        if (startTime == null || endTime == null) {
            throw new InvalidBookingException("Start and end time are required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new InvalidBookingException("End time must be after start time");
        }
        if (!startTime.isAfter(now)) {
            throw new InvalidBookingException("Start time must be in the future");
        }
    }
}
