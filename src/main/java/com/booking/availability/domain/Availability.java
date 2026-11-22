package com.booking.availability.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Availability {

    private static final Duration MAX_WINDOW = Duration.ofHours(24);

    private final Long id;
    private final Long providerId;
    private final Instant startTime;
    private final Instant endTime;
    private AvailabilityStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Availability(Long id, Long providerId, Instant startTime, Instant endTime,
                         AvailabilityStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Availability create(Long providerId, Instant startTime, Instant endTime, Instant now) {
        requireProviderId(providerId);
        requireWindow(startTime, endTime, now);
        return new Availability(null, providerId, startTime, endTime, AvailabilityStatus.OPEN, now, now);
    }

    public static Availability restore(Long id, Long providerId, Instant startTime, Instant endTime,
                                       AvailabilityStatus status, Instant createdAt, Instant updatedAt) {
        return new Availability(id, providerId, startTime, endTime, status, createdAt, updatedAt);
    }

    public void open(Instant now) {
        this.status = AvailabilityStatus.OPEN;
        this.updatedAt = now;
    }

    public void close(Instant now) {
        this.status = AvailabilityStatus.CLOSED;
        this.updatedAt = now;
    }

    public boolean isOpen() {
        return status == AvailabilityStatus.OPEN;
    }

    public boolean overlaps(Instant otherStart, Instant otherEnd) {
        return startTime.isBefore(otherEnd) && otherStart.isBefore(endTime);
    }

    public boolean canHold(TimeSlot slot) {
        return isOpen() && !slot.start().isBefore(startTime) && !slot.end().isAfter(endTime);
    }

    public List<TimeSlot> slots(Duration slotDuration) {
        if (slotDuration == null || slotDuration.isZero() || slotDuration.isNegative()) {
            throw new InvalidAvailabilityException("Slot duration must be positive");
        }
        List<TimeSlot> slots = new ArrayList<>();
        Instant cursor = startTime;
        while (!cursor.plus(slotDuration).isAfter(endTime)) {
            Instant slotEnd = cursor.plus(slotDuration);
            slots.add(new TimeSlot(cursor, slotEnd));
            cursor = slotEnd;
        }
        return List.copyOf(slots);
    }

    public Long getId() {
        return id;
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

    public AvailabilityStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static void requireProviderId(Long providerId) {
        if (providerId == null) {
            throw new InvalidAvailabilityException("Availability must belong to a provider");
        }
    }

    private static void requireWindow(Instant startTime, Instant endTime, Instant now) {
        if (startTime == null || endTime == null) {
            throw new InvalidAvailabilityException("Start and end time are required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new InvalidAvailabilityException("End time must be after start time");
        }
        if (!startTime.isAfter(now)) {
            throw new InvalidAvailabilityException("Start time must be in the future");
        }
        if (Duration.between(startTime, endTime).compareTo(MAX_WINDOW) > 0) {
            throw new InvalidAvailabilityException("Availability window must not exceed 24 hours");
        }
    }
}
