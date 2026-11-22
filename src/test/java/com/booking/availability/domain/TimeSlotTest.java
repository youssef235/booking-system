package com.booking.availability.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class TimeSlotTest {

    private static final Instant T1 = Instant.parse("2026-01-02T12:00:00Z");
    private static final Instant T2 = Instant.parse("2026-01-02T12:30:00Z");
    private static final Instant T3 = Instant.parse("2026-01-02T13:00:00Z");

    @Test
    void slotWithStartNotBeforeEndFails() {
        assertThrows(InvalidAvailabilityException.class, () -> new TimeSlot(T2, T1));
        assertThrows(InvalidAvailabilityException.class, () -> new TimeSlot(T1, T1));
    }

    @Test
    void overlappingSlotsAreDetected() {
        assertTrue(new TimeSlot(T1, T3).overlaps(new TimeSlot(T2, T3)));
    }

    @Test
    void touchingSlotsDoNotOverlap() {
        assertFalse(new TimeSlot(T1, T2).overlaps(new TimeSlot(T2, T3)));
    }
}
