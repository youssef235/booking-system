package com.booking.availability.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AvailabilityTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant START = Instant.parse("2026-01-02T12:00:00Z");
    private static final Instant END = Instant.parse("2026-01-02T14:00:00Z");

    private Availability window() {
        return Availability.create(5L, START, END, NOW);
    }

    @Test
    void createBuildsOpenAvailability() {
        Availability availability = window();

        assertEquals(AvailabilityStatus.OPEN, availability.getStatus());
        assertTrue(availability.isOpen());
        assertEquals(5L, availability.getProviderId());
        assertEquals(START, availability.getStartTime());
    }

    @Test
    void createWithStartInPastFails() {
        assertThrows(InvalidAvailabilityException.class,
                () -> Availability.create(5L, Instant.parse("2025-12-31T12:00:00Z"), END, NOW));
    }

    @Test
    void createWithEndBeforeStartFails() {
        assertThrows(InvalidAvailabilityException.class, () -> Availability.create(5L, END, START, NOW));
    }

    @Test
    void createWithWindowOver24HoursFails() {
        assertThrows(InvalidAvailabilityException.class,
                () -> Availability.create(5L, START, START.plus(Duration.ofHours(25)), NOW));
    }

    @Test
    void createWithoutProviderFails() {
        assertThrows(InvalidAvailabilityException.class, () -> Availability.create(null, START, END, NOW));
    }

    @Test
    void slotsSplitsWindowEvenly() {
        List<TimeSlot> slots = window().slots(Duration.ofMinutes(30));

        assertEquals(4, slots.size());
        assertEquals(new TimeSlot(START, START.plus(Duration.ofMinutes(30))), slots.get(0));
        assertEquals(END, slots.get(3).end());
    }

    @Test
    void slotsKeepsWholeSlotsOnly() {
        List<TimeSlot> slots = window().slots(Duration.ofMinutes(45));

        assertEquals(2, slots.size());
        assertEquals(START.plus(Duration.ofMinutes(90)), slots.get(1).end());
    }

    @Test
    void slotsWithNonPositiveDurationFails() {
        assertThrows(InvalidAvailabilityException.class, () -> window().slots(Duration.ZERO));
    }

    @Test
    void overlapsDetectsOverlapAndIgnoresTouchingEdges() {
        Availability availability = window();

        assertTrue(availability.overlaps(START.minus(Duration.ofHours(1)), START.plus(Duration.ofMinutes(1))));
        assertTrue(availability.overlaps(START.plus(Duration.ofMinutes(30)), END.minus(Duration.ofMinutes(30))));
        assertFalse(availability.overlaps(END, END.plus(Duration.ofHours(1))));
        assertFalse(availability.overlaps(START.minus(Duration.ofHours(1)), START));
    }

    @Test
    void canHoldSlotInsideOpenWindow() {
        Availability availability = window();

        assertTrue(availability.canHold(new TimeSlot(START, START.plus(Duration.ofMinutes(30)))));
        assertTrue(availability.canHold(new TimeSlot(END.minus(Duration.ofMinutes(30)), END)));
    }

    @Test
    void closedAvailabilityCannotHoldSlot() {
        Availability availability = window();

        availability.close(NOW);

        assertFalse(availability.canHold(new TimeSlot(START, START.plus(Duration.ofMinutes(30)))));
    }

    @Test
    void slotOutsideWindowCannotBeHeld() {
        Availability availability = window();

        assertFalse(availability.canHold(new TimeSlot(END.minus(Duration.ofMinutes(15)), END.plus(Duration.ofMinutes(15)))));
    }
}
