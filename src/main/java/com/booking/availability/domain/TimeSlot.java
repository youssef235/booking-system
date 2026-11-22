package com.booking.availability.domain;

import java.time.Instant;

public record TimeSlot(Instant start, Instant end) {

    public TimeSlot {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new InvalidAvailabilityException("Slot start must be before slot end");
        }
    }

    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }
}
